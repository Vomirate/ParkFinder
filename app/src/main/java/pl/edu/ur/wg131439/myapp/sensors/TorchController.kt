package pl.edu.ur.wg131439.myapp.sensors

import android.content.Context
import android.hardware.camera2.CameraManager

class TorchController(context: Context) {
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    fun hasTorch(): Boolean {
        return try {
            cameraManager.cameraIdList.any { id ->
                val chars = cameraManager.getCameraCharacteristics(id)
                val flash = chars.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                flash
            }
        } catch (_: Exception) {
            false
        }
    }

    fun setTorch(enabled: Boolean) {
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                val chars = cameraManager.getCameraCharacteristics(id)
                val flash = chars.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                flash
            } ?: return
            cameraManager.setTorchMode(cameraId, enabled)
        } catch (_: Exception) {
            // ignore
        }
    }
}
