package itu.cs.mukesh.codescanner

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.tv_textView

private const val CAMERA_REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {

    private lateinit var codeScannar: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpPermission()
        codeScanner()
    }

    private fun codeScanner(){
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        val tv_textView : TextView = findViewById(R.id.tv_textView)

        codeScannar = CodeScanner(this, scannerView)

        codeScannar.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = true

            decodeCallback = DecodeCallback {
                runOnUiThread{
                    tv_textView.text = it.text
                }
            }
            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main","Camera Initialization Error : ${it.message}")
                }
            }
            scannerView.setOnClickListener {
                codeScannar.startPreview()
            }
        }

    }
    override fun onResume(){
        super.onResume()
        codeScannar.startPreview()
    }

    override fun onPause() {
        codeScannar.releaseResources()
        super.onPause()
    }

    private fun setUpPermission(){
        val permission = ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)

        if(permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }

    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
        CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            CAMERA_REQUEST_CODE ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"You need the Camera permission to be able to use this app",
                        Toast.LENGTH_SHORT)
                }else{
                    //Successful
                }
            }
        }
    }
}