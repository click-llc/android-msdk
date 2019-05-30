package uz.click.mobilesdk.impl

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.fragment_scan.*
import uz.click.mobilesdk.R
import uz.click.mobilesdk.utils.ValidationUtils
import java.util.regex.Pattern


/**
 * @author rahmatkhujaevs on 29/01/19
 * */
class ScanFragment : AppCompatDialogFragment() {

    private var cameraSource: CameraSource? = null

    companion object {
        const val TAG = "DETECTOR"
        const val REQUEST_CAMERA = 10001
        const val REGEX_FOR_CARD_VALIDATION = "[0-9bs]{4} [0-9bs]{4} [0-9bs]{4} [0-9bs]{4}"
        const val REGEX_EXPIRED_DATE = "[0-9bsS]{2}/[0-9bs]{2}"
    }
    var number = ""
    var date = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }
    init {
        setStyle(STYLE_NO_FRAME, R.style.cl_FullscreenDialogTheme)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startCameraSource()
    }

    private fun startCameraSource() {
        val textRecognizer = TextRecognizer.Builder(context).build()

        if (!textRecognizer.isOperational) {
            Log.w(TAG, "Detector dependencies not loaded yet")
        } else {

            cameraSource = CameraSource.Builder(context, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build()

            surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

                }

                override fun surfaceDestroyed(holder: SurfaceHolder?) {
                    cameraSource?.stop()
                }

                override fun surfaceCreated(holder: SurfaceHolder?) {
                    if (ActivityCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        requestPermissions(
                            arrayOf(
                                Manifest.permission.CAMERA
                            ),
                            REQUEST_CAMERA
                        )
                        return
                    }
                    cameraSource?.start(surfaceView.holder)
                }

            })

            textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
                override fun release() {

                }

                override fun receiveDetections(detections: Detector.Detections<TextBlock>?) {
                    if (detections?.detectedItems?.size() != 0) {

                        for (i in 0 until detections?.detectedItems?.size()!!) {
                            val textBlock = detections.detectedItems.valueAt(i)
                            val patternCardNumber = Pattern.compile(REGEX_FOR_CARD_VALIDATION)
                            val matcherCardNumber = patternCardNumber.matcher(textBlock.value)
                            while (matcherCardNumber.find()) {
                                number = matcherCardNumber.group().replace('b','6').replace('s','5')
                            }

                            val patternCardExpiredDate = Pattern.compile(REGEX_EXPIRED_DATE)
                            val matcherCardExpired = patternCardExpiredDate.matcher(textBlock.value)
                            while (matcherCardExpired.find()) {
                                date = matcherCardExpired.group().replace('b','6').replace('s','5')
                            }
                        }
                        if (number.isNotEmpty() && date.isNotEmpty()) {
                            parentFragment?.let {
                                val parent = parentFragment as MainDialogFragment
                                parent.setScannedData(number, date)
                            }
                        }
                    }
                }

            })
        }
    }
}