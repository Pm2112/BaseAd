//package com.pdm.basead.audio
//
//import android.media.MediaRecorder
//import android.os.Environment
//import android.util.Log
//import java.io.File
//import java.io.IOException
//
//class AudioRecorder {
//    private val tag = "DebugAudioRecorder"
//    private var mediaRecorder: MediaRecorder? = null
//    private var audioFile: File? = null
//    private var isRecording = false
//
//    @Throws(IOException::class)
//    fun startRecording(outputFile: File): File {
//        if (isRecording) {
//            throw IllegalStateException("Recording is already in progress")
//        }
//
//        // Đặt file đầu ra
//        audioFile = outputFile
//
//        // Kiểm tra phiên bản API và sử dụng MediaRecorder tương thích với tất cả các phiên bản
//        mediaRecorder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//            // MediaRecorder.Builder chỉ có trên API 31 trở lên
//            try {
//                MediaRecorder.Builder()
//                    .setAudioSource(MediaRecorder.AudioSource.MIC)
//                    .setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//                    .setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
//                    .setOutputFile(audioFile?.absolutePath)
//                    .build()
//            } catch (e: Exception) {
//                throw IOException("Lỗi khi khởi tạo MediaRecorder Builder: ${e.message}")
//            }
//        } else {
//            // Cách này sẽ hoạt động trên các phiên bản API cũ hơn
//            MediaRecorder().apply {
//                setAudioSource(MediaRecorder.AudioSource.MIC)
//                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
//                setOutputFile(audioFile?.absolutePath)
//            }
//        }
//
//        try {
//            mediaRecorder?.prepare()
//            mediaRecorder?.start()
//        } catch (e: Exception) {
//            throw IOException("Lỗi khi bắt đầu ghi âm: ${e.message}")
//        }
//
//        isRecording = true
//        return audioFile ?: throw IOException("Không thể tạo file đầu ra")
//    }
//
//    // Stop recording
//    fun stopRecording(): File? {
//        if (!isRecording) {
//            throw IllegalStateException("Recording is not in progress")
//        }
//
//        try {
//            mediaRecorder?.apply {
//                stop()
//                release()
//            }
//        } catch (e: IllegalStateException) {
//            // Handle invalid MediaRecorder state
//            Log.e(tag, "Illegal state while stopping recording: ${e.message}")
//        } catch (e: IOException) {
//            // Handle I/O errors
//            Log.e(tag, "I/O error while stopping recording: ${e.message}")
//        } catch (e: SecurityException) {
//            // Handle security issues (e.g., missing microphone permissions)
//            Log.e(tag, "Security error while stopping recording: ${e.message}")
//        } finally {
//            mediaRecorder = null
//            isRecording = false
//        }
//
//        return audioFile
//    }
//
//    // Check if recording is active
//    fun isRecording(): Boolean {
//        return isRecording
//    }
//}