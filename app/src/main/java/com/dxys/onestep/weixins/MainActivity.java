package com.dxys.onestep.weixins;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "正在启动微信扫一扫...", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec("su");
                    DataOutputStream os = new DataOutputStream(process.getOutputStream());
                    os.write("am start com.tencent.mm/.plugin.scanner.ui.BaseScanUI\nexit\n".getBytes());
                    os.close();
                    if (process.waitFor() == 0)
                    {
                        StringBuilder successMsg = new StringBuilder();
                        StringBuilder errorMsg = new StringBuilder();
                        BufferedReader successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        BufferedReader errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        String s;
                        while ((s = successResult.readLine()) != null) {
                            successMsg.append(s);
                        }
                        while ((s = errorResult.readLine()) != null) {
                            errorMsg.append(s);
                        }
                        if (errorMsg.toString().isEmpty() || errorMsg.toString().startsWith("Warning"))
                        {
                            if (successMsg.toString().startsWith("Starting: Intent")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "启动成功！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "启动失败，无Root权限！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "启动失败，未安装微信！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        finish();
                    }else {
                        process.destroy();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "启动失败，无Root权限！", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "启动失败，无Root权限！", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "启动失败，未知错误！", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        }).start();
        moveTaskToBack(false);
    }
}
