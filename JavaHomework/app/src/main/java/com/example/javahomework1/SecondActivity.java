/*   时间：2019/10/01
*    作者：南开大学1613415 catnip
*    注释：2019年秋JAVA作业1
* */

package com.example.javahomework1;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static com.example.javahomework1.baseClass.card;
import static com.example.javahomework1.baseClass.consumInfos;

public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu2);
    }

    //1、本月账单查询
    public void showAmountDetail(View v)
    {
        final String curNumber = this.getIntent().getStringExtra("number");
        AlertDialog.Builder InfoDialog = new AlertDialog.Builder(SecondActivity.this);
        InfoDialog.setTitle("本月账单查询"); //标题
        card.get(curNumber).ShowMsg(InfoDialog);
    }

    //2、套餐余量查询
    public void showRemainDetail(View v)
    {
        final String curNumber = this.getIntent().getStringExtra("number");
        AlertDialog.Builder InfoDialog = new AlertDialog.Builder(SecondActivity.this);
        card.get(curNumber).SerPackage.ShowInfo(InfoDialog, curNumber);
    }

    //3、打印消费详单
    public void showDescription(View v)
    {
        final String curNumber = this.getIntent().getStringExtra("number");

        AlertDialog.Builder InfoDialog = new AlertDialog.Builder(SecondActivity.this);
        InfoDialog.setTitle(curNumber + "消费记录"); //标题
        String content = "";
        if(!consumInfos.containsKey(curNumber))
        {
            content += "暂无数据";
        }
        else {
            for (int i = 0; i < consumInfos.get(curNumber).size(); i++) {
                String line = consumInfos.get(curNumber).get(i).type + "       " +
                              consumInfos.get(curNumber).get(i).consum + consumInfos.get(curNumber).get(i).tail + "\n";
                content += line;
            }
        }
        InfoDialog.setMessage(content);
        InfoDialog.setNegativeButton("返回", null);
        InfoDialog.show();
    }

    //4、套餐变更
    public void changingPack(View v)
    {
        final String curNumber = this.getIntent().getStringExtra("number");

        AlertDialog.Builder InfoDiaglog = new AlertDialog.Builder(SecondActivity.this);
        InfoDiaglog.setTitle("套餐变更"); //标题
        final String[] Pack = {"话痨套餐", "网虫套餐", "超人套餐"};
        InfoDiaglog.setSingleChoiceItems(Pack, -1,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(card.get(curNumber).SerPackage.GetPackageName().equals(Pack[which])){
                    AlertDialog.Builder result = new AlertDialog.Builder(SecondActivity.this);
                    result.setTitle("错误");
                    result.setMessage("对不起，您已是该套餐的用户，无需换套餐！");
                    result.show();
                }
                else{
                    baseClass.ServicePackage temp;
                    if(which == 0)
                        temp = new baseClass().new TalkPackage();
                    else if(which == 1)
                        temp = new baseClass().new NetPackage();
                    else
                        temp = new baseClass().new SuperPackage();

                    if(card.get(curNumber).Money < temp.price){
                        AlertDialog.Builder result = new AlertDialog.Builder(SecondActivity.this);
                        result.setTitle("错误");
                        result.setMessage("对不起，您的余额不足以支付新套餐本月资费，请先充值后再办理更换套餐业务！");
                        result.show();
                    }
                    else{
                        card.get(curNumber).Money -= temp.price;
                        card.get(curNumber).ConsumAmount += temp.price;
                        card.get(curNumber).SerPackage = temp;
                        card.get(curNumber).RealSMSCount = 0;
                        card.get(curNumber).RealTalkTime = 0;
                        card.get(curNumber).RealFlow = 0;
                        AlertDialog.Builder result = new AlertDialog.Builder(SecondActivity.this);
                        result.setTitle("套餐变更成功");
                        String content = "套餐变更为 " + Pack[which] + "\n" +
                                         "当前余额为 " + card.get(curNumber).Money;
                        result.setMessage(content);
                        result.show();
                    }}}});
        InfoDiaglog.show();
    }

    //5、办理退网
    public void delCard(View v){
        final String curNumber = this.getIntent().getStringExtra("number");
        AlertDialog.Builder InfoDiaglog = new AlertDialog.Builder(SecondActivity.this);
        InfoDiaglog.setTitle("退网办理"); //标题
        String content = "当前账号为：" + curNumber +"\n确认办理退网吗？";
        InfoDiaglog.setMessage(content);   //内容
        InfoDiaglog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface judge_dialog, int which) {
                        card.remove(curNumber);
                        if(baseClass.consumInfos.containsKey(curNumber))
                            consumInfos.remove(curNumber);
                        AlertDialog.Builder QuicDiaglog = new AlertDialog.Builder(SecondActivity.this);
                        QuicDiaglog.setTitle("退网成功");
                        QuicDiaglog.setMessage("卡号" + curNumber + "办理退网成功！\n谢谢使用！");
                        QuicDiaglog.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface judge_dialog, int which) {
                                        Intent MyIntent = new Intent();
                                        setResult(RESULT_OK, MyIntent);
                                        finish();
                                    }});
                        QuicDiaglog.show();
                    }});
        InfoDiaglog.setNegativeButton("取消", null);
        InfoDiaglog.show();
    }

    //6、返回上一页
    public void TurnBack(View v) {
        Intent MyIntent = new Intent();
        setResult(RESULT_OK, MyIntent);
        finish();
    }
}
