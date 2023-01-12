package com.meeting.flutter_screen_led

import android.os.SystemClock
import android.util.Log
import java.io.*

class GpioUtils {

    companion object{
        private val TAG = "GpioUtils"

        fun upgradeRootPermissionForExport() {
            upgradeRootPermission("/sys/class/gpio/export")
        }

        fun exportGpio(gpio: Int): Boolean {
            return writeNode("/sys/class/gpio/export", "" + gpio)
        }


        fun upgradeRootPermissionForGpio(gpio: Int) {
            upgradeRootPermission("/sys/class/gpio/gpio$gpio/direction")
            upgradeRootPermission("/sys/class/gpio/gpio$gpio/value")
        }


        private fun upgradeRootPermission(path: String): Boolean {
            var process: Process? = null
            var os: DataOutputStream? = null
            try {
                val cmd = "chmod 777 $path"
                process = Runtime.getRuntime().exec("su")
                os = DataOutputStream(process.outputStream)
                os.writeBytes(
                    """
                $cmd
                
                """.trimIndent()
                )
                os.writeBytes("exit\n")
                os.flush()
                process.waitFor()
            } catch (ignored: Exception) {
            } finally {
                try {
                    os?.close()
                    assert(process != null)
                    process!!.destroy()
                } catch (ignored: Exception) {
                }
            }
            try {
                return process!!.waitFor() == 0
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return false
        }

        fun setGpioDirection(gpio: Int, arg: Int): Boolean {
            var gpioDirection = ""
            gpioDirection = if (arg == 0) "out" else if (arg == 1) "in" else return false
            return writeNode("/sys/class/gpio/gpio$gpio/direction", gpioDirection)
        }

        fun getGpioDirection(gpio: Int): String? {
            var gpioDirection: String? = ""
            gpioDirection = readNode("/sys/class/gpio/gpio$gpio/direction")
            return gpioDirection
        }

        fun writeGpioValue(gpio: Int, arg: String?): Boolean {
            return writeNode("/sys/class/gpio/gpio$gpio/value", arg)
        }

        fun getGpioValue(gpio: Int): String? {
            return readNode("/sys/class/gpio/gpio$gpio/value")
        }

        private fun writeNode(path: String?, arg: String?): Boolean {
            Log.d(TAG, "Gpio test set node path:" + path + "arg:" + arg)
            if (path == null || arg == null) {
                Log.e(TAG, "set node error")
                return false
            }
            var fileWriter: FileWriter? = null
            val bufferedWriter: BufferedWriter? = null
            try {
                fileWriter = FileWriter(path)
                fileWriter.write(arg)
            } catch (e: Exception) {
                Log.e(TAG, "Gpio_test write node error!! path$path arg: $arg")
                e.printStackTrace()
                return false
            } finally {
                try {
                    fileWriter?.close()
                    bufferedWriter?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return true
        }

        private val mTime: Long = 0
        private var mFailTimes = 0
        private fun readNode(path: String): String? {
            var result: String? = ""
            var fread: FileReader? = null
            var buffer: BufferedReader? = null
            try {
                fread = FileReader(path)
                buffer = BufferedReader(fread)
                var str: String? = null
                while (buffer.readLine().also { str = it } != null) {
                    result = str
                    break
                }
                mFailTimes = 0
            } catch (e: IOException) {
                Log.e(TAG, "IO Exception")
                e.printStackTrace()
                if (mTime == 0L || SystemClock.uptimeMillis() - mTime < 1000) {
                    mFailTimes++
                }
                if (mFailTimes >= 3) {
                    Log.d(TAG, "read format node continuous failed three times,exist thread")
                }
            } finally {
                try {
                    buffer?.close()
                    fread?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return result
        }
    }
}