package com.amap.location.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.amap.loc.diagnose.problem.DiagnoseActivity;

/**
 * @author hongming.wang
 * @date 2018/9/27
 * @mail hongming.whm@alibaba-inc.com
 */
public class DiagnoseDemo_Activity extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnose_demo);
        setTitle(R.string.locDiagnose_title);
        Button startDiagnose = (Button) findViewById(R.id.btn_startDiagnose);
        startDiagnose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DiagnoseDemo_Activity.this, DiagnoseActivity.class));
            }
        });
    }
}
