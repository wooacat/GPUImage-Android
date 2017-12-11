package com.test;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.gpuimage.GPUImageProcessingQueue;
import com.gpuimage.GPUImageRenderer;
import com.gpuimage.R;
import com.gpuimage.mediautils.GMediaVideoReader;
import com.gpuimage.outputs.GPUImageView;
import com.gpuimage.sources.GPUImageMovie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    private GPUImageMovie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GPUImageRenderer gpuImageRenderer = new GPUImageRenderer();
        final GPUImageView glSurfaceView = findViewById(R.id.iv_test);
        glSurfaceView.setGPUImageRenderer(gpuImageRenderer);

        GPUImageProcessingQueue.sharedQueue().setThreadRequest(new GPUImageProcessingQueue.ThreadRequest() {
            @Override
            public void request() {
                glSurfaceView.requestRender();
            }
        });

        mMovie = new GPUImageMovie();
        mMovie.addTarget(glSurfaceView);

        Button bt = (Button) findViewById(R.id.bt_test);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.avi";

                        InputStream inputStream = null;
                        try {
                            inputStream = getApplicationContext().getAssets().open("Megamind.avi");
                            writeToFile(inputStream, path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        GMediaVideoReader videoReader = new GMediaVideoReader();
                        videoReader.loadMP4(path);
                        while (videoReader.readFrame()) {
                            try {
                                mMovie.processMovieFrame(videoReader.getNV12Data(), videoReader.getFrameWidth(), videoReader.getFrameHeight(), videoReader.getTimestamp());
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).run();



/*
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test);
                if (test)
                    iv_test.setImageBitmap(bitmap);
                else {
                    byte[] data = new byte[bitmap.getWidth()*bitmap.getHeight()*4];
                    bitmap.copyPixelsToBuffer(ByteBuffer.wrap(data));
                    OceanUtils.enhance(data, bitmap.getWidth(), bitmap.getHeight(), data);
                    bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(data));
                    iv_test.setImageBitmap(bitmap);
                }
                test = !test;
//                iv_test.setImageResource(R.drawable.test);
*/
            }
        });
    }

    private String writeToFile(InputStream is, String filepath)
            throws IOException {
        if (is == null || TextUtils.isEmpty(filepath))
            return null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {

            File file = new File(filepath);
            in = new BufferedInputStream(is);
            out = new BufferedOutputStream(new FileOutputStream(filepath));
            byte[] buffer = new byte[4096];
            int l;
            while ((l = in.read(buffer)) != -1)
                out.write(buffer, 0, l);
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (in != null)
                    in.close();
                is.close();
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException ioe) {
            }
        }
        return filepath;
    }

    private void writeBitmap(Bitmap bitmap, String name) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/gpuimage/" + name;
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        File f= new File(path);
        if (f.exists()) f.delete();
        try {
            if (f.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
