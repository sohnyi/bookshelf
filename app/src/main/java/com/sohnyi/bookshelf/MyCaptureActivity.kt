package com.sohnyi.bookshelf

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.sohnyi.bookshelf.capture.MyCaptureManager
import com.sohnyi.bookshelf.databinding.ActivityScanBinding
import com.sohnyi.bookshelf.utils.setStatusBarMode

class MyCaptureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding

    private var capture: MyCaptureManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarMode(Color.WHITE, true)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        capture = MyCaptureManager(this, binding.barcodeScanner)
        capture?.let { lifecycle.addObserver(it) }
        capture?.initializeFromIntent(intent, savedInstanceState)
        capture?.decode()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture?.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return binding.barcodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }
}