package com.example.callmonitor

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.callmonitor.ui.MyApp
import com.example.callmonitor.ui.typography
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val PHONE_STATE_REQ_CODE = 123

    private val MY_TAG = "ZAKI_TAG"

    private lateinit var myPrefs: SharedPreferences

    private lateinit var editor: SharedPreferences.Editor

    private lateinit var work: OneTimeWorkRequest

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StartPoint()
        }
        myPrefs = getSharedPreferences("Zaki", MODE_PRIVATE)

        askForPermission()
        JavaCode.requestRole(this)

    }

    @SuppressLint("CommitPrefEdits")
    @Composable
    fun StartPoint() {

        val prefBool = myPrefs.getBoolean("bool", false)

        editor = myPrefs.edit()

        val isShow = remember { mutableStateOf(!prefBool) }

        val list = listOf("1", "3", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60")

        val isOpen = remember { mutableStateOf(false) }

        val selectedStr = remember { mutableStateOf("1") }

        val openCloseOfDropDown: (Boolean) -> Unit = {
                isOpen.value = it
        }

        val selectedString: (String) -> Unit = {
            selectedStr.value = it
        }

        val onClick: () -> Unit = {
            isOpen.value = true
        }

        val textValue = remember { mutableStateOf("") }

        val changeValue: (String) -> Unit = {
                textValue.value = it
        }

        val buttonText = remember { mutableStateOf( "Set") }

        val openDialog = remember { mutableStateOf(false) }

        val onDismissRequest: () -> Unit = {
            openDialog.value = false
        }

        if (prefBool) {
            WorkManager.getInstance(this)
                .getWorkInfosByTagLiveData(MY_TAG).observe(this) {
                    val workInfo = it[0]
                    if (workInfo.state.name == WorkInfo.State.SUCCEEDED.name) {
                        isShow.value = true
                    }
                }
        }

        MyApp {

                Column(
                    modifier = Modifier
                        .fillMaxSize(1f)
                        .padding(top = 120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (isShow.value) {

                        MainScreen(
                            textValue = textValue.value,
                            onValueChange = changeValue,
                            selectedStr = selectedStr.value,
                            onClick = onClick,
                            requestToOpen = isOpen.value,
                            list = list,
                            request = openCloseOfDropDown,
                            selectedString = selectedString,
                        )

                    }

                    Button(onClick = {
                        if (!isShow.value) {
                            editor.putBoolean("bool", false)
                            editor.apply()

                            WorkManager.getInstance(this@MainActivity)
                                .cancelAllWorkByTag(MY_TAG)

                            buttonText.value = "Set"
                            isShow.value = true

                        } else {

                            if (textValue.value.isNotEmpty()) {
                                editor.putString("MSG", textValue.value)
                                editor.putBoolean("bool", true)
                                editor.apply()

                                work = OneTimeWorkRequestBuilder<CallWorker>()
                                    .setInitialDelay(selectedStr.value.toLong(), TimeUnit.MINUTES)
                                    .addTag(MY_TAG)
                                    .build()

                                WorkManager.getInstance(this@MainActivity).enqueue(work)
                                openDialog.value = true

                            } else {
                                Toast.makeText(this@MainActivity, "Please Enter Your Message",
                                    Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }
                    },

                     modifier = Modifier.padding(top = 40.dp)
                    ) {
                        Text(text = if (isShow.value) buttonText.value else "Cancel")
                    }
                }
                if (openDialog.value) {
                    ShowAlertDialog(onDismissRequest = onDismissRequest, selectedStr.value)
                }
            }
    }


    @Composable
    fun MainScreen(
        textValue: String,
        onValueChange: (String) -> Unit,
        selectedStr: String,
        onClick: () -> Unit,
        requestToOpen: Boolean,
        list: List<String>,
        request: (Boolean) -> Unit,
        selectedString: (String) -> Unit,
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                text = "For How Many Minutes :",
                style = typography.h6,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )

            ShowSpinner(selectedString = selectedStr, onClick = onClick)

            Spinner(
                requestToOpen = requestToOpen,
                list = list,
                request = request,
                selectedString = selectedString
            )

        }

        OutlinedTextField(
            value = textValue,
            onValueChange = onValueChange,
            label = { Text(text = stringResource(id = R.string.hintText)) },
            modifier = Modifier.padding(top = 24.dp),
        )
    }



    @Composable
    private fun ShowSpinner(
        selectedString: String,
        onClick: () -> Unit
    ) {

        Row(
            modifier = Modifier
                .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                .size(width = 120.dp, height = 32.dp)
                .clickable {
                    onClick()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = selectedString,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(
                    start = 8.dp, top = 4.dp, bottom = 4.dp
                )
            )

            Image(
                painter = painterResource(id = R.drawable.ic_down),
                contentDescription = "",
                alignment = Alignment.CenterEnd,
                modifier = Modifier.padding(end = 8.dp, top = 4.dp, bottom = 4.dp)
            )

        }
    }

    @Composable
    private fun Spinner(
        requestToOpen: Boolean,
        list: List<String>,
        request: (Boolean) -> Unit,
        selectedString: (String) -> Unit
    ) {

            DropdownMenu(
                expanded = requestToOpen,
                onDismissRequest = {
                    request(false)
                }
            ) {
                list.forEach {
                    DropdownMenuItem(onClick = {
                        request(false)
                        selectedString(it)
                    }) {

                        Text(
                            text = it,
                            modifier = Modifier.fillMaxWidth()
                        )

                    }
                }

            }
    }


    @Composable
    fun ShowAlertDialog(
        onDismissRequest: () -> Unit,
        minutes: String
    ) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Alert")
        },

        text = {
            Text(text = "it Will Decline all Calls for $minutes Minute(s).")
        },
        confirmButton = {
            Button(onClick = {
                onDismissRequest()
                finish()
            }) {
                Text(text = "OK")
            }
        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.all {
                it == PackageManager.PERMISSION_GRANTED
            }) {
            Toast.makeText(this, "Thank you", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please Grant All Permission", Toast.LENGTH_SHORT).show()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun askForPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.MANAGE_OWN_CALLS,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.SEND_SMS)
        } else {
            arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.SEND_SMS)
        }

        if (!checkPermissions(permissions)) {
            requestPermissions(permissions, PHONE_STATE_REQ_CODE)
        }
    }

    private fun checkPermissions(permissions: Array<String>) : Boolean {
        return permissions.all { permission ->
            checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
    }

}