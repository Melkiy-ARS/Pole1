package com.example.pole;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    ArrayList<Word> wordList;
    String data;
    ImageButton add,delete;
    TextView textView;
    private final static String FILE_NAME = "database.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        add = (ImageButton) findViewById(R.id.imageButtonAdd);
        delete = (ImageButton) findViewById(R.id.imageButtonDelete);
        textView = (TextView) findViewById(R.id.textView);
        wordList = new ArrayList<Word>();
        data = load();
        update(data);
        //###########################################################//
        View.OnClickListener addListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setTitle("Новое слово");
                builder.setMessage("Введите новое слово");
                final EditText input = new EditText(AdminActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                final String addText = "&" + input.getText();
                                dialog.cancel();
                                //Старт нового Диалога
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(AdminActivity.this);
                                builder2.setTitle("Новое слово");
                                builder2.setMessage("Введите описание слова");
                                final EditText input2 = new EditText(AdminActivity.this);
                                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                input2.setLayoutParams(lp2);
                                builder2.setView(input2);
                                builder2.setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int which) {
                                                data += addText + "@" + input2.getText();
                                                if(data.charAt(0)=='&')data = data.substring(1);//На случай добавления первого слова в список
                                                save(data); //Сохраняем все данные с новым словом
                                                update(data);
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog2 = builder2.create();
                                alertDialog2.show();
                            }
                        });
                builder.setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        };
        add.setOnClickListener(addListener);
        //###########################################################//
        View.OnClickListener deleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setTitle("Удаление слова");
                builder.setMessage("Введите слово");
                final EditText input = new EditText(AdminActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);
                builder.setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.cancel();
                            }
                        });
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                for(int i = 0; i < wordList.size(); i++){
                                    if(input.getText().toString().equals(wordList.get(i).getWord())){
                                        data = data.replaceFirst("&"+wordList.get(i).getWord()+"@"+wordList.get(i).getDescription(), "");
                                        //На случай удаление первого слова, да костыль
                                        data = data.replaceFirst(wordList.get(i).getWord()+"@"+wordList.get(i).getDescription()+"&", "");
                                        break;
                                    }
                                }
                                save(data);
                                update(data);
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }; delete.setOnClickListener(deleteListener);
    }

    public String load(){ //Метод для полученния данных из файла
        String text = "";
        FileInputStream fin = null;
        try {
            fin = openFileInput(FILE_NAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            text = new String (bytes);
            Log.d("Load of data",text);
        }
        catch(IOException ex) {
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{
            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){
                Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return text;
    }

    public void save(String data){ //Метод для сохранения данных в файл
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(data.getBytes());
        } catch (IOException ex) {
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally { //В любом случае мы обьязаны закрыть поток
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException ex) {
                Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void update(String data){ //Обновление списка
        wordList.clear();
        if(data.length()!=0) {
            String[] loops = data.split("&");
            Log.d("Update of data",data);
            String text = "";
            for (int i = 0; i < loops.length; i++) {
                Word word = new Word(loops[i]);
                wordList.add(word);
                text += word.getWord() + "\n" + word.getDescription() + "\n";
            }
            textView.setText(text);
        }
    }
}