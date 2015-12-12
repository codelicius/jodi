package makasa.dapurkonten.jodohideal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import makasa.dapurkonten.jodohideal.app.SQLiteController;

import java.util.HashMap;


public class questionsActivity extends AppCompatActivity{
    private SQLiteController db;
    TextView pertanyaans,idquestion;
    Button goto_next,goto_prev;
    RadioGroup groupQuestion;
    RadioButton question1,question2;
    int idpertanyaan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idpertanyaan = 1;
        setContentView(R.layout.activity_question);
        pertanyaans = (TextView)findViewById(R.id.questiont);
        idquestion = (TextView)findViewById(R.id.idQuestiont);
        goto_next = (Button)findViewById(R.id.goto_next);
        goto_prev = (Button)findViewById(R.id.goto_previous);
        groupQuestion = (RadioGroup)findViewById(R.id.groupQuestion);
        question1 = (RadioButton)findViewById(R.id.question1);
        question2 = (RadioButton)findViewById(R.id.question2);
        goto_prev.setVisibility(View.INVISIBLE);
        db = new SQLiteController(getApplicationContext());
        HashMap<String,String> tenQuestion = db.getIdQuestion(idpertanyaan);
        String id=tenQuestion.get("id"),
                question_id=tenQuestion.get("question_id"),
                question=tenQuestion.get("question"),
                answer_ops1=tenQuestion.get("answer_ops1"),
                answer_ops2=tenQuestion.get("answer_ops2");
        pertanyaans.setText(question);
        idquestion.setText(id);
        question1.setText(answer_ops1);
        question2.setText(answer_ops2);


    }
    public void next (View view){
        if(idpertanyaan<10) {
            idpertanyaan++;
            groupQuestion.clearCheck();
            if(idpertanyaan==10){
                goto_next.setText("Finish");
            }
            HashMap<String,String> tenQuestion = db.getIdQuestion(idpertanyaan);
            String id=tenQuestion.get("id"),
                    question_id=tenQuestion.get("question_id"),
                    question=tenQuestion.get("question"),
                    answer_ops1=tenQuestion.get("answer_ops1"),
                    answer_ops2=tenQuestion.get("answer_ops2");
            pertanyaans.setText(question);
            idquestion.setText(id);
            question1.setText(answer_ops1);
            question2.setText(answer_ops2);
            goto_prev.setVisibility(View.VISIBLE);
        }
    }
    public void previous (View view){
        if(idpertanyaan>1) {
            idpertanyaan--;
            if(idpertanyaan==1){
                goto_prev.setVisibility(View.INVISIBLE);
            }
            groupQuestion.clearCheck();
            HashMap<String,String> tenQuestion = db.getIdQuestion(idpertanyaan);
            String id=tenQuestion.get("id"),
                    question_id=tenQuestion.get("question_id"),
                    question=tenQuestion.get("question"),
                    answer_ops1=tenQuestion.get("answer_ops1"),
                    answer_ops2=tenQuestion.get("answer_ops2");
            pertanyaans.setText(question);
            idquestion.setText(id);
            question1.setText(answer_ops1);
            question2.setText(answer_ops2);
            goto_next.setText("Next");

        }
    }
}
