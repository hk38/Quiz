package com.lifeistech.android.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    // 問題を管理するリスト
    private ArrayList<Question> question_list = new ArrayList<>();
    // 描画更新用Handler
    private Handler handler;
    // 1問あたりの制限時間(sec)
    private int time = 10;
    // 問題数
    private int question_num;
    // 現在の問題
    private Question current_question = null;
    // 残り時間を表示するプログレスバー
    private ProgressBar progress;
    // 残りの制限時間(sec*10)
    private int rest_time;
    // 現在の問題番号
    private int current = 0;
    // 正解を選んだ数
    private int correct_num;

    // TODO [01] ここから
    // 問題表示用TextView
    private TextView question;
    // ステータス表示用TextView
    private TextView states;
    // 問題画像表示用ImageView
    private ImageView image;
    // 上段のボタン
    private Button buttonHi;
    // 中段のボタン
    private Button buttonMid;
    // 下段のボタン
    private Button buttonLow;
    // TODO [01] ここまで

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();

        // TODO [02]ここから
        // 各変数をレイアウト上のViewと結びつける
        question = (TextView)findViewById(R.id.question_text);
        states = (TextView)findViewById(R.id.status);
        image = (ImageView)findViewById(R.id.question_image);
        progress = (ProgressBar)findViewById(R.id.progressBar);
        buttonHi = (Button)findViewById(R.id.button1);
        buttonMid = (Button)findViewById(R.id.button2);
        buttonLow = (Button)findViewById(R.id.button3);
        progress.setMax(time * 100);

        // 問題を作成し，開始
        makeQuestions();
        startQuestion();
        // TODO [02] ここまで
    }

    // 問題を作成する
    private void makeQuestions() {
        // TODO [03] ここから
        // 問題を作成
        Question q1 = new Question(R.drawable.japan, "日本の首都は？", "東京", "京都", "大阪");
        Question q2 = new Question(R.drawable.japan, "一番大きな都道府県は？", "北海道", "長野", "岡山");
        Question q3 = new Question(R.drawable.japan, "一番小さな都道府県は？", "香川", "東京", "大阪");
        Question q4 = new Question(R.drawable.japan, "海に面していない都道府県は？", "埼玉", "京都", "佐賀");
        Question q5 = new Question(R.drawable.japan, "最南端の島がある都道府県は？", "東京", "沖縄", "鹿児島");
        // リストに追加
        question_list.add(q1);
        question_list.add(q2);
        question_list.add(q3);
        question_list.add(q4);
        question_list.add(q5);
        // TODO [03] ここまで
    }

    // 問題の表示を始める
    private void startQuestion() {
        question_num = question_list.size();
        nextQuestion();
    }

    private void nextQuestion() {
        if (current >= question_num) {
            current = -1;
            // 次の問題がもう無い時
            // 結果画面に移動
            Intent i = new Intent(this, ResultActivity.class);
            i.putExtra("QUESTION", question_num);
            i.putExtra("CORRECT", correct_num);
            startActivity(i);
            // そのままにしておくと画面が積み重なっていくので終了させる
            finish();
            return;
        }
        // TODO [04] ここから
        // ステータス表示用TextViewに現在の問題数，現在の正解数，残りの問題数を表示
        states.setText(String.valueOf(current) + "問中" + String.valueOf(correct_num) + "問正解\n" + "残り" + String.valueOf(question_num - current) + "問");

        // 問題番号の位置と同じ位置の問題を取得
        current_question = question_list.get(current);
        // 問題文を表示
        question.setText(current_question.question_text);
        // 問題の画像をセット
        image.setImageResource(current_question.image_id);
        // 選択肢の文字配列を取得
        String[] choices_text = current_question.getChoices();
        // それぞれのボタンに選択肢の文字列を表示
        buttonHi.setText(choices_text[0]);
        buttonMid.setText(choices_text[1]);
        buttonLow.setText(choices_text[2]);

        // 問題番号を次にする
        current = current + 1;

        startTimeLimitThread();
        // TODO [04] ここまで
    }

    // ボタンがタップされた時に呼ばれるイベントリスナー
    public void click(View v) {
        // TODO [05] ここから
        // 押したボタンのテキストを取得し文字列に変換
        String buttonText = ((Button) v).getText().toString();

        // 答えと一致するかチェック
        if(buttonText.equals(current_question.answer)){
            // 正解数＋1
            correct_num = correct_num + 1;
        }

        // 次の問題へ
        nextQuestion();
        // TODO [05] ここまで
    }

    /**
     * 1問ごとの制限時間を管理するスレッドを起動する
     */
    private void startTimeLimitThread() {
        rest_time = time * 100;
        progress.setProgress(rest_time);
        // このThreadが担当する問題番号
        final int local_current = current;
        Thread t = new Thread() {
            @Override
            public void run() {
                while (rest_time >= 0) {
                    if (local_current != current) {
                        // すでにボタンをタップして次の問題に進んでいる
                        return;
                    }
                    try {
                        // 10ミリ秒待機
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            rest_time -= 1;
                            progress.setProgress(rest_time);
                        }
                    });
                }
                // まだ問題に解答していない場合
                if (local_current == current) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            nextQuestion();
                        }
                    });
                }
            }
        };
        t.start();
    }

    /**
     * 問題を管理するクラス
     */
    class Question {
        /**
         * 画面に表示する画像のリソースID
         */
        private final int image_id;
        /**
         * 問題文として表示する文字列
         */
        private final String question_text;
        /**
         * 正解の答え
         */
        private final String answer;
        /**
         * 不正解の答え①
         */
        private final String wrong_1;
        /**
         * 不正解の答え②
         */
        private final String wrong_2;

        public Question(int image_id, String question_text, String answer, String wrong_1, String wrong_2) {
            this.image_id = image_id;
            this.question_text = question_text;
            this.answer = answer;
            this.wrong_1 = wrong_1;
            this.wrong_2 = wrong_2;
        }

        /**
         * シャッフルした問題の選択肢を返すメソッド
         */
        public String[] getChoices() {
            // ボタンの位置をランダムにする
            String choices_tmp[] = {answer, wrong_1, wrong_2};
            // 配列からリストへ
            List<String> list = Arrays.asList(choices_tmp);
            // リストをシャッフル
            Collections.shuffle(list);
            // リストをStringの配列にする
            return list.toArray(new String[3]);
        }
    }
}
