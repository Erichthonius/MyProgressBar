package example.com.myprogressbar;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private int counts;//计数
    private boolean isRunning;//子线程的开关
    private MyProgressBar myProgressBar;//声明控件
    private Thread thread;//子线程
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1){
                ++counts;//模拟进度
                if (counts == 100){
                    isRunning = false;//关闭子线程
                    return false;
                }
                myProgressBar.setProgress(counts);
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
       myProgressBar=(MyProgressBar)findViewById(R.id.test_myprogressbar);
    }
    /**
     * 按钮的点击事件
     * @param view
     */
    public void start(View view){
        if (thread !=null && thread.isAlive()){
            Toast.makeText(this,"子线程没有运行结束！",Toast.LENGTH_SHORT).show();
            return;
        }
        //设置一个子线程来模拟进度
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                counts = 0;
                while (isRunning){
                    try {
                        Thread.sleep(2000);
                        handler.sendEmptyMessage(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        Toast.makeText(this,"子线程开始运行了！",Toast.LENGTH_SHORT).show();
    }
}
