package com.lifeistech.android.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends ActionBarActivity {
    /**
     * 結果表示用TextView
     */
    private TextView result;
    /**
     * メッセージ表示用TextView
     */
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // View関連をフィールドへ
        result = (TextView) findViewById(R.id.result);
        message = (TextView) findViewById(R.id.message);

        setResultData();
    }

    private void setResultData() {
        Intent i = getIntent();
        // 問題数
        int question_num = i.getIntExtra("QUESTION", 0);
        // 正解数
        int correct_num = i.getIntExtra("CORRECT", 0);

        result.setText(String.valueOf(correct_num) + "問正解");
        if (question_num == correct_num) {
            // 全問正解
            message.setText("パーフェクトおめでとう！");
        } else if ((float)  correct_num / question_num >= 0.8) {
            // 正答率80％以上
            message.setText("パーフェクトまであと少しだ！");
        } else {
            message.setText("もっと頑張ってみよう！");
        }
    }

    /**
     * バックボタンが押された時にタイトル画面へ戻る
     */
    @Override
    public void onBackPressed() {
        backToTitle();
    }

    public void click(View v) {
        backToTitle();
    }

    /**
     * タイトル画面へ戻ります
     */
    private void backToTitle() {
        Intent i = new Intent(this, StartActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }
}
