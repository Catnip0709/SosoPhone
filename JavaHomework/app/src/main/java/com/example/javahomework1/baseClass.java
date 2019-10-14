/*    时间：2019/10/01
 *    作者：南开大学1613415 catnip
 *    注释：2019年秋JAVA作业1
 * */
package com.example.javahomework1;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//接口1：打电话
interface CallService{
    void call(AlertDialog.Builder alterDialog, baseClass.MobileCard curCard, int act);
}
//接口2：发短信
interface SendService{
    void send(AlertDialog.Builder alterDialog, baseClass.MobileCard curCard, int act);
}
//接口3：上网
interface NetService{
    void netPlay(AlertDialog.Builder alterDialog, baseClass.MobileCard curCard, int act);
}

public class baseClass extends AppCompatActivity
{
    //***************************** 服务包 *************************************//
    public enum ACTION
    {
        EM_TALKTIME, EM_SMSCOUNT, EM_FLOW
    }
    //服务包类
    abstract class ServicePackage
    {
        double price;
        abstract String GetPackageName();
        abstract void ShowInfo(AlertDialog.Builder alterDialog, String curNumber);
    }
    //服务包类子类1：话痨套餐
    class TalkPackage extends baseClass.ServicePackage implements CallService, SendService
    {
        int TalkTime; //通话时间
        int smsCount; //短信条数
        TalkPackage()
        {
            price    = 58;
            TalkTime = 200;
            smsCount = 50;
        }
        public String GetPackageName(){
            return "话痨套餐";
        }
        public void ShowInfo(AlertDialog.Builder alterDialog, String curNumber) //展示套餐信息
        {
            alterDialog.setTitle("套餐余量查询");     //标题
            double showTalkTime;
            if(TalkTime > card.get(curNumber).RealTalkTime) //套餐中通话时间还没用完
                showTalkTime = TalkTime - card.get(curNumber).RealTalkTime;
            else  //套餐中通话时间已经耗尽
                showTalkTime = 0;

            double showsmsCount;
            if(smsCount > card.get(curNumber).RealSMSCount) //套餐中短信条数还没用完
                showsmsCount = smsCount - card.get(curNumber).RealSMSCount;
            else  //套餐中短信条数已经耗尽
                showsmsCount = 0;
            String content = "您的卡号：" + curNumber     + "\n套餐内结余\n" +
                             "通话时长：" + showTalkTime  + "分钟\n" +
                             "短信条数：" + showsmsCount  + "条";
            alterDialog.setMessage(content);   //内容
            alterDialog.show();
        }
        public void call(AlertDialog.Builder alterDiaglog, baseClass.MobileCard curCard, int act)  //打电话服务接口
        {
            String content = "消费目的：" + scene.get(act).type + "\n";

            int leftTalkTime = TalkTime - baseClass.card.get(curCard.CardNumber).RealTalkTime; //套餐剩余通话时间
            if(leftTalkTime >= scene.get(act).description)  // 套餐剩余通话时间 >= 本次需要使用的时间
            {   // 成功消费
                if(!consumInfos.containsKey(curCard.CardNumber)){
                    consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                    content += "消费结果：消费成功。不存在此卡的消费记录，已添加一条消费信息\n";
                }
                else{
                    content += "消费结果：消费成功。";
                }
                baseClass.card.get(curCard.CardNumber).RealTalkTime += scene.get(act).description; //更新该账号的已通话总时长
                ConsumInfo temp = new ConsumInfo(ACTION.EM_TALKTIME, scene.get(act).description, scene.get(act).price);
                consumInfos.get(curCard.CardNumber).add(temp);
            }
            else // 套餐剩余通话时间 < 本次需要使用的时间
            {
                int NeedMoneyTalkTime = scene.get(act).description - leftTalkTime; //需要额外付费的通话时间
                double NeedMoney = NeedMoneyTalkTime * 0.2; //额外需要的钱
                if(baseClass.card.get(curCard.CardNumber).Money >= NeedMoney) // 余额 >= 额外需要的钱
                {
                    if(!consumInfos.containsKey(curCard.CardNumber)){
                        consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                        content += "消费结果：消费成功。不存在此卡的消费记录，已添加一条消费信息。";
                    }
                    else{
                        content += "消费结果：消费成功。";
                    }
                    baseClass.card.get(curCard.CardNumber).RealTalkTime += scene.get(act).description; //更新该账号的已通话总时长
                    baseClass.card.get(curCard.CardNumber).Money -= NeedMoney; //更新账户余额
                    ConsumInfo temp = new ConsumInfo(ACTION.EM_TALKTIME, scene.get(act).description, scene.get(act).price);
                    consumInfos.get(curCard.CardNumber).add(temp);
                    content += "套餐通话时间已耗尽，额外花费" + NeedMoney + "元，账户余额" + baseClass.card.get(curCard.CardNumber).Money + "元";
                }
                else // 余额 < 额外需要的钱
                {
                    if(!consumInfos.containsKey(curCard.CardNumber)){
                        consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                        content += "消费结果：消费失败。不存在此卡的消费记录，已添加一条消费信息。";
                    }
                    else{
                        content += "消费结果：消费失败。";
                    }
                    double UseTime = leftTalkTime + baseClass.card.get(curCard.CardNumber).Money/0.2; //消耗的通话时间 = 套餐剩余时间 + 剩下的余额可以通话的时间
                    baseClass.card.get(curCard.CardNumber).RealTalkTime += UseTime; //更新该账号的已通话总时长
                    baseClass.card.get(curCard.CardNumber).Money = 0; //更新账户余额
                    ConsumInfo temp = new ConsumInfo(ACTION.EM_TALKTIME, (int)UseTime, scene.get(act).price);
                    consumInfos.get(curCard.CardNumber).add(temp);
                    content += "本次已通话" + (int)UseTime + "分钟，您的余额不足，请充值后再消费。";
                }
            }

            alterDiaglog.setMessage(content);
            alterDiaglog.setNegativeButton("返回", null);
            alterDiaglog.show();
        }
        public void send(AlertDialog.Builder alterDiaglog, baseClass.MobileCard curCard, int act) //发短信服务接口
        {
            String content = "消费目的：" + scene.get(act).type + "\n";

            int leftSMS = smsCount - baseClass.card.get(curCard.CardNumber).RealSMSCount; //套餐剩余短信条数
            if(leftSMS >= scene.get(act).description)  // 套餐剩余短信条数 >= 本次需要使用的短信条数
            {   // 成功消费
                if(!consumInfos.containsKey(curCard.CardNumber)){
                    consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                    content += "消费结果：消费成功。不存在此卡的消费记录，已添加一条消费信息\n";
                }
                else{
                    content += "消费结果：消费成功。";
                }
                baseClass.card.get(curCard.CardNumber).RealSMSCount += scene.get(act).description; //更新该账号的已使用短信条数
                ConsumInfo temp = new ConsumInfo(ACTION.EM_SMSCOUNT, scene.get(act).description, scene.get(act).price);
                consumInfos.get(curCard.CardNumber).add(temp);
            }
            else // 套餐剩余短信条数 < 本次需要使用的短信条数
            {
                int NeedMoneySMS = scene.get(act).description - leftSMS; //需要额外付费的短信条数
                double NeedMoney = NeedMoneySMS * 0.1; //额外需要的钱
                if(baseClass.card.get(curCard.CardNumber).Money >= NeedMoney) // 余额 >= 额外需要的钱
                {
                    if(!consumInfos.containsKey(curCard.CardNumber)){
                        consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                        content += "消费结果：消费成功。不存在此卡的消费记录，已添加一条消费信息。";
                    }
                    else{
                        content += "消费结果：消费成功。";
                    }
                    baseClass.card.get(curCard.CardNumber).RealSMSCount += scene.get(act).description; //更新该账号的已使用短信条数
                    baseClass.card.get(curCard.CardNumber).ConsumAmount += NeedMoney; //更新账户总消费
                    baseClass.card.get(curCard.CardNumber).Money -= NeedMoney;        //更新账户余额
                    ConsumInfo temp = new ConsumInfo(ACTION.EM_SMSCOUNT, scene.get(act).description, scene.get(act).price);
                    consumInfos.get(curCard.CardNumber).add(temp);
                    content += "套餐短信已耗尽，额外花费" + NeedMoney + "元，账户余额" + baseClass.card.get(curCard.CardNumber).Money + "元";
                }
                else // 余额 < 额外需要的钱
                {
                    if(!consumInfos.containsKey(curCard.CardNumber)){
                        consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                        content += "消费结果：消费失败。不存在此卡的消费记录，已添加一条消费信息。";
                    }
                    else{
                        content += "消费结果：消费失败。";
                    }
                    double UseSMS = leftSMS + baseClass.card.get(curCard.CardNumber).Money/0.1; //消耗的短信条数 = 套餐剩余短信条数 + 剩下的余额可以通话的时间
                    baseClass.card.get(curCard.CardNumber).RealTalkTime += UseSMS;              //更新该账号的已使用短信条数
                    baseClass.card.get(curCard.CardNumber).ConsumAmount += baseClass.card.get(curCard.CardNumber).Money; //更新账户总消费
                    baseClass.card.get(curCard.CardNumber).Money = 0; //更新账户余额
                    ConsumInfo temp = new ConsumInfo(ACTION.EM_TALKTIME, (int)UseSMS, scene.get(act).price);
                    consumInfos.get(curCard.CardNumber).add(temp);
                    content += "本次已发送短信" + (int)UseSMS + "条，您的余额不足，请充值后再消费。";
                }
            }

            alterDiaglog.setMessage(content);
            alterDiaglog.setNegativeButton("返回", null);
            alterDiaglog.show();
        }
    }
    //服务包类子类2：超人套餐
    class SuperPackage extends baseClass.ServicePackage implements CallService, SendService, NetService
    {
        int TalkTime;
        int smsCount;
        int flow;
        SuperPackage()
        {
            price    = 78;
            TalkTime = 200;
            smsCount = 100;
            flow     = 1024;
        }
        public String GetPackageName()
        {
            return "超人套餐";
        }
        public void ShowInfo(AlertDialog.Builder alterDialog, String curNumber){ //展示剩余套餐
            alterDialog.setTitle("套餐余量查询");//标题
            double showTalkTime, showsmsCount, showFlow;

            if(TalkTime > card.get(curNumber).RealTalkTime) //套餐中通话时间还没用完
                showTalkTime = TalkTime - card.get(curNumber).RealTalkTime;
            else //套餐中通话时间已经耗尽
                showTalkTime = 0;

            if(smsCount > card.get(curNumber).RealSMSCount) //套餐中短信条数还没用完
                showsmsCount =smsCount - card.get(curNumber).RealSMSCount;
            else //套餐中短信条数已经耗尽
                showsmsCount = 0;

            if(flow > card.get(curNumber).RealFlow)  //套餐中流量还没用完
                showFlow = flow - card.get(curNumber).RealFlow;
            else  //套餐中流量已经耗尽
                showFlow = 0;

            DecimalFormat format = new DecimalFormat("0.0");
            String content = "您的卡号：" + curNumber     + "\n套餐内结余\n" +
                             "通话时长：" + showTalkTime  + "分钟\n"  +
                             "短信条数：" + showsmsCount  + "条\n"  +
                             "上网流量：" + format.format(showFlow/1024.0) + "GB";
            alterDialog.setMessage(content);   //内容
            alterDialog.show();
        }
        public void call(AlertDialog.Builder alterDialog, baseClass.MobileCard curCard, int act) //打电话服务接口
        {
            String content = "消费目的：" + scene.get(act).type + "\n";

            int leftTalkTime = TalkTime - baseClass.card.get(curCard.CardNumber).RealTalkTime; //套餐剩余通话时间
            if(leftTalkTime >= scene.get(act).description)  // 套餐剩余通话时间 >= 本次需要使用的时间
            {   // 成功消费
                if(!consumInfos.containsKey(curCard.CardNumber)){
                    consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                    content += "消费结果：消费成功。不存在此卡的消费记录，已添加一条消费信息\n";
                }
                else{
                    content += "消费结果：消费成功。";
                }
                baseClass.card.get(curCard.CardNumber).RealTalkTime += scene.get(act).description; //更新该账号的已通话总时长
                ConsumInfo temp = new ConsumInfo(ACTION.EM_TALKTIME, scene.get(act).description, scene.get(act).price);
                consumInfos.get(curCard.CardNumber).add(temp);
            }
            else // 套餐剩余通话时间 < 本次需要使用的时间
            {
                int NeedMoneyTalkTime = scene.get(act).description - leftTalkTime; //需要额外付费的通话时间
                double NeedMoney = NeedMoneyTalkTime * 0.2; //额外需要的钱
                if(baseClass.card.get(curCard.CardNumber).Money >= NeedMoney) // 余额 >= 额外需要的钱
                {
                    if(!consumInfos.containsKey(curCard.CardNumber)){
                        consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                        content += "消费结果：消费成功。不存在此卡的消费记录，已添加一条消费信息。";
                    }
                    else{
                        content += "消费结果：消费成功。";
                    }
                    baseClass.card.get(curCard.CardNumber).RealTalkTime += scene.get(act).description; //更新该账号的已通话总时长
                    baseClass.card.get(curCard.CardNumber).ConsumAmount += NeedMoney; //更新账户总消费
                    baseClass.card.get(curCard.CardNumber).Money -= NeedMoney; //更新账户余额
                    ConsumInfo temp = new ConsumInfo(ACTION.EM_TALKTIME, scene.get(act).description, scene.get(act).price);
                    consumInfos.get(curCard.CardNumber).add(temp);
                    content += "套餐通话时间已耗尽，额外花费" + NeedMoney + "元，账户余额" + baseClass.card.get(curCard.CardNumber).Money + "元";
                }
                else // 余额 < 额外需要的钱
                {
                    if(!consumInfos.containsKey(curCard.CardNumber)){
                        consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                        content += "消费结果：消费失败。不存在此卡的消费记录，已添加一条消费信息。";
                    }
                    else{
                        content += "消费结果：消费失败。";
                    }
                    double UseTime = leftTalkTime + baseClass.card.get(curCard.CardNumber).Money/0.2; //消耗的通话时间 = 套餐剩余时间 + 剩下的余额可以通话的时间
                    baseClass.card.get(curCard.CardNumber).RealTalkTime += UseTime; //更新该账号的已通话总时长
                    baseClass.card.get(curCard.CardNumber).ConsumAmount += baseClass.card.get(curCard.CardNumber).Money; //更新账户总消费
                    baseClass.card.get(curCard.CardNumber).Money = 0; //更新账户余额
                    ConsumInfo temp = new ConsumInfo(ACTION.EM_TALKTIME, (int)UseTime, scene.get(act).price);
                    consumInfos.get(curCard.CardNumber).add(temp);
                    content += "本次已通话" + (int)UseTime + "分钟，您的余额不足，请充值后再消费。";
                }
            }

            alterDialog.setMessage(content);
            alterDialog.setNegativeButton("返回", null);
            alterDialog.show();
        }
        public void send(AlertDialog.Builder alterDialog, baseClass.MobileCard curCard, int act)    //发短信服务接口
        {
            String content = "消费目的：" + scene.get(act).type + "\n";

            int leftSMS = smsCount - baseClass.card.get(curCard.CardNumber).RealSMSCount; //套餐剩余短信条数
            if(leftSMS >= scene.get(act).description)  // 套餐剩余短信条数 >= 本次需要使用的短信条数
            {   // 成功消费
                if(!consumInfos.containsKey(curCard.CardNumber)){
                    consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                    content += "消费结果：消费成功。不存在此卡的消费记录，已添加一条消费信息\n";
                }
                else{
                    content += "消费结果：消费成功。";
                }
                baseClass.card.get(curCard.CardNumber).RealSMSCount += scene.get(act).description; //更新该账号的已使用短信条数
                ConsumInfo temp = new ConsumInfo(ACTION.EM_SMSCOUNT, scene.get(act).description, scene.get(act).price);
                consumInfos.get(curCard.CardNumber).add(temp);
            }
            else // 套餐剩余短信条数 < 本次需要使用的短信条数
            {
                int NeedMoneySMS = scene.get(act).description - leftSMS; //需要额外付费的短信条数
                double NeedMoney = NeedMoneySMS * 0.1; //额外需要的钱
                if(baseClass.card.get(curCard.CardNumber).Money >= NeedMoney) // 余额 >= 额外需要的钱
                {
                    if(!consumInfos.containsKey(curCard.CardNumber)){
                        consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                        content += "消费结果：消费成功。不存在此卡的消费记录，已添加一条消费信息。";
                    }
                    else{
                        content += "消费结果：消费成功。";
                    }
                    baseClass.card.get(curCard.CardNumber).RealSMSCount += scene.get(act).description; //更新该账号的已使用短信条数
                    baseClass.card.get(curCard.CardNumber).ConsumAmount += NeedMoney; //更新账户总消费
                    baseClass.card.get(curCard.CardNumber).Money -= NeedMoney;        //更新账户余额
                    ConsumInfo temp = new ConsumInfo(ACTION.EM_SMSCOUNT, scene.get(act).description, scene.get(act).price);
                    consumInfos.get(curCard.CardNumber).add(temp);
                    content += "套餐短信已耗尽，额外花费" + NeedMoney + "元，账户余额" + baseClass.card.get(curCard.CardNumber).Money + "元";
                }
                else // 余额 < 额外需要的钱
                {
                    if(!consumInfos.containsKey(curCard.CardNumber)){
                        consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                        content += "消费结果：消费失败。不存在此卡的消费记录，已添加一条消费信息。";
                    }
                    else{
                        content += "消费结果：消费失败。";
                    }
                    double UseSMS = leftSMS + baseClass.card.get(curCard.CardNumber).Money/0.1; //消耗的短信条数 = 套餐剩余短信条数 + 剩下的余额可以通话的时间
                    baseClass.card.get(curCard.CardNumber).RealTalkTime += UseSMS;              //更新该账号的已使用短信条数
                    baseClass.card.get(curCard.CardNumber).ConsumAmount += baseClass.card.get(curCard.CardNumber).Money; //更新账户总消费
                    baseClass.card.get(curCard.CardNumber).Money = 0; //更新账户余额
                    ConsumInfo temp = new ConsumInfo(ACTION.EM_TALKTIME, (int)UseSMS, scene.get(act).price);
                    consumInfos.get(curCard.CardNumber).add(temp);
                    content += "本次已发送短信" + (int)UseSMS + "条，您的余额不足，请充值后再消费。";
                }
            }

            alterDialog.setMessage(content);
            alterDialog.setNegativeButton("返回", null);
            alterDialog.show();
        }
        public void netPlay(AlertDialog.Builder alterDialog, baseClass.MobileCard curCard, int act) //上网服务接口
        {
            String content = "消费目的：" + scene.get(act).type + "\n";

            int leftNet = flow - baseClass.card.get(curCard.CardNumber).RealSMSCount; //套餐剩余流量
            if(leftNet >= scene.get(act).description)  // 套餐剩余流量 >= 本次需要使用的流量
            {   // 成功消费
                if(!consumInfos.containsKey(curCard.CardNumber)){
                    consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                    content += "消费结果：消费成功。不存在此卡的消费记录，已添加一条消费信息\n";
                }
                else{
                    content += "消费结果：消费成功。";
                }
                baseClass.card.get(curCard.CardNumber).RealFlow += scene.get(act).description; //更新该账号的已流量
                ConsumInfo temp = new ConsumInfo(ACTION.EM_FLOW, scene.get(act).description, scene.get(act).price);
                consumInfos.get(curCard.CardNumber).add(temp);
            }
            else // 套餐剩余流量 < 本次需要使用的流量
            {
                int NeedMoneyFlow = scene.get(act).description - leftNet; //需要额外付费的流量
                double NeedMoney = NeedMoneyFlow * 0.1; //额外需要的钱
                if(baseClass.card.get(curCard.CardNumber).Money >= NeedMoney) // 余额 >= 额外需要的钱
                {
                    if(!consumInfos.containsKey(curCard.CardNumber)){
                        consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                        content += "消费结果：消费成功。不存在此卡的消费记录，已添加一条消费信息。";
                    }
                    else{
                        content += "消费结果：消费成功。";
                    }
                    baseClass.card.get(curCard.CardNumber).RealFlow += scene.get(act).description; //更新该账号的已使用流量
                    baseClass.card.get(curCard.CardNumber).ConsumAmount += NeedMoney; //更新账户总消费
                    baseClass.card.get(curCard.CardNumber).Money -= NeedMoney;        //更新账户余额
                    ConsumInfo temp = new ConsumInfo(ACTION.EM_FLOW, scene.get(act).description, scene.get(act).price);
                    consumInfos.get(curCard.CardNumber).add(temp);
                    content += "套餐流量已耗尽，额外花费" + NeedMoney + "元，账户余额" + baseClass.card.get(curCard.CardNumber).Money + "元";
                }
                else // 余额 < 额外需要的钱
                {
                    if(!consumInfos.containsKey(curCard.CardNumber)){
                        consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                        content += "消费结果：消费失败。不存在此卡的消费记录，已添加一条消费信息。";
                    }
                    else{
                        content += "消费结果：消费失败。";
                    }
                    double UseFlow = leftNet + baseClass.card.get(curCard.CardNumber).Money/0.1; //消耗的流量 = 套餐剩余流量 + 剩下的余额可以转换成的流量
                    baseClass.card.get(curCard.CardNumber).RealTalkTime += UseFlow;              //更新该账号的已使用流量
                    baseClass.card.get(curCard.CardNumber).ConsumAmount += baseClass.card.get(curCard.CardNumber).Money; //更新账户总消费
                    baseClass.card.get(curCard.CardNumber).Money = 0; //更新账户余额
                    ConsumInfo temp = new ConsumInfo(ACTION.EM_FLOW, (int)UseFlow, scene.get(act).price);
                    consumInfos.get(curCard.CardNumber).add(temp);
                    content += "本次已使用流量" + (int)UseFlow + "MB，您的余额不足，请充值后再消费。";
                }
            }

            alterDialog.setMessage(content);
            alterDialog.setNegativeButton("返回", null);
            alterDialog.show();
        }
    }
    //服务包类子类3：网虫套餐
    class NetPackage extends baseClass.ServicePackage implements NetService
    {
        int flow;

        NetPackage()
        {
            price = 68;
            flow  = 5120;
        }

        public String GetPackageName()
        {
            return "网虫套餐";
        }
        public void ShowInfo(AlertDialog.Builder alterDiaglog, String curNumber) //展示剩余套餐
        {
            alterDiaglog.setTitle("套餐余量查询");     //标题
            DecimalFormat format = new DecimalFormat("#.0");
            int showFlow;
            if (flow > card.get(curNumber).RealFlow) //未超出，返回剩余的量
                showFlow = flow - card.get(curNumber).RealFlow;
            else
                showFlow = 0;
            String content = "您的卡号：" + curNumber + "\n套餐内结余\n" +
                             "上网流量：" + format.format(showFlow / 1024.0) + "GB";
            alterDiaglog.setMessage(content);   //内容
            alterDiaglog.show();

        }
        public void netPlay(AlertDialog.Builder alterDiaglog, baseClass.MobileCard curCard, int act) //上网服务接口
        {
            String content = "消费目的：" + scene.get(act).type + "\n";

            int leftNet = flow - baseClass.card.get(curCard.CardNumber).RealSMSCount; //套餐剩余流量
            if(leftNet >= scene.get(act).description)  // 套餐剩余流量 >= 本次需要使用的流量
            {   // 成功消费
                if(!consumInfos.containsKey(curCard.CardNumber)){
                    consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                    content += "消费结果：消费成功。不存在此卡的消费记录，已添加一条消费信息\n";
                }
                else{
                    content += "消费结果：消费成功。";
                }
                baseClass.card.get(curCard.CardNumber).RealFlow += scene.get(act).description; //更新该账号的已流量
                ConsumInfo temp = new ConsumInfo(ACTION.EM_FLOW, scene.get(act).description, scene.get(act).price);
                consumInfos.get(curCard.CardNumber).add(temp);
            }
            else // 套餐剩余流量 < 本次需要使用的流量
            {
                int NeedMoneyFlow = scene.get(act).description - leftNet; //需要额外付费的流量
                double NeedMoney = NeedMoneyFlow * 0.1; //额外需要的钱
                if(baseClass.card.get(curCard.CardNumber).Money >= NeedMoney) // 余额 >= 额外需要的钱
                {
                    if(!consumInfos.containsKey(curCard.CardNumber)){
                        consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                        content += "消费结果：消费成功。不存在此卡的消费记录，已添加一条消费信息。";
                    }
                    else{
                        content += "消费结果：消费成功。";
                    }
                    baseClass.card.get(curCard.CardNumber).RealFlow += scene.get(act).description; //更新该账号的已使用流量
                    baseClass.card.get(curCard.CardNumber).ConsumAmount += NeedMoney; //更新账户总消费
                    baseClass.card.get(curCard.CardNumber).Money -= NeedMoney;        //更新账户余额
                    ConsumInfo temp = new ConsumInfo(ACTION.EM_FLOW, scene.get(act).description, scene.get(act).price);
                    consumInfos.get(curCard.CardNumber).add(temp);
                    content += "套餐流量已耗尽，额外花费" + NeedMoney + "元，账户余额" + baseClass.card.get(curCard.CardNumber).Money + "元";
                }
                else // 余额 < 额外需要的钱
                {
                    if(!consumInfos.containsKey(curCard.CardNumber)){
                        consumInfos.put(curCard.CardNumber, new ArrayList<ConsumInfo>());
                        content += "消费结果：消费失败。不存在此卡的消费记录，已添加一条消费信息。";
                    }
                    else{
                        content += "消费结果：消费失败。";
                    }
                    double UseFlow = leftNet + baseClass.card.get(curCard.CardNumber).Money/0.1; //消耗的流量 = 套餐剩余流量 + 剩下的余额可以转换成的流量
                    baseClass.card.get(curCard.CardNumber).RealTalkTime += UseFlow;              //更新该账号的已使用流量
                    baseClass.card.get(curCard.CardNumber).ConsumAmount += baseClass.card.get(curCard.CardNumber).Money; //更新账户总消费
                    baseClass.card.get(curCard.CardNumber).Money = 0; //更新账户余额
                    ConsumInfo temp = new ConsumInfo(ACTION.EM_FLOW, (int)UseFlow, scene.get(act).price);
                    consumInfos.get(curCard.CardNumber).add(temp);
                    content += "本次已使用流量" + (int)UseFlow + "MB，您的余额不足，请充值后再消费。";
                }
            }

            alterDiaglog.setMessage(content);
            alterDiaglog.setNegativeButton("返回", null);
            alterDiaglog.show();
        }
    }
    //***************************************************************************//

    //手机卡类
    class MobileCard
    {
        String CardNumber;         //卡号
        String UserName;           //用户名
        String PassWord;           //密码
        baseClass.ServicePackage SerPackage; //服务包
        double ConsumAmount;       //总消费额
        double Money;              //余额
        int RealTalkTime;          //实际通话时间
        int RealSMSCount;          //实际短信数量
        int RealFlow;              //实际流量消耗

        MobileCard()
        {
            this.RealTalkTime = 0;
            this.RealSMSCount = 0;
            this.RealFlow     = 0;
        }

        void ShowMsg(AlertDialog.Builder alterDialog)
        {
            DecimalFormat format = new DecimalFormat("#.0");
            String content = "卡号："       + CardNumber                   + "\n"     +
                             "用户名："     + UserName                     + "\n"     +
                             "服务包："     + SerPackage.GetPackageName()  + "\n"     +
                             "套餐资费："   + SerPackage.price             + "元\n"   +
                             "合计消费额：" + ConsumAmount                 + "元\n"   +
                             "账户余额："   + format.format(Money)         + "元\n"   +
                             "通话时间：" + RealTalkTime                   + "分钟\n" +
                             "短信数量：" + RealSMSCount                   + "条\n"   +
                             "流量消耗：" + RealFlow                       + "MB\n";

            alterDialog.setMessage(content);   //内容
            alterDialog.show();
        }
    }
    //消费记录
    class ConsumInfo
    {
        ConsumInfo(ACTION act, int count, double m_price){
            consum = count;
            switch (act){
                case EM_TALKTIME:
                    tail = "分钟";
                    type = "通话";
                    break;
                case EM_SMSCOUNT:
                    tail = "条";
                    type = "短信";
                    break;
                case EM_FLOW:
                    tail = "MB";
                    type = "上网";
                    break;
                default:
                    assert false: "wrong input in ConsumInfo";
            }
            price = m_price;
        }
        int consum;   // 消费数量
        double price; // 消费价格
        String type;  // 消费类型：通话、短信、上网
        String tail;  // 单位
    }
    //使用场景
    class Scene
    {
        Scene(String m_type, ACTION m_data, int m_description, double m_price)
        {
            type = m_type;
            data = m_data;
            description = m_description;
            price = m_price;
        }
        String type;      // 描述场景
        ACTION data;      // 消费类型
        int description;  // 消费详情
        double price;     // 本消费场景造成的消费金额
    }

    //******************************************************************//

    static Map<String, baseClass.MobileCard> card              = new HashMap(); // 嗖嗖移动用户列表
    static Map<String, List<baseClass.ConsumInfo>> consumInfos = new HashMap(); // 消费信息
    static Map<Integer, baseClass.Scene> scene                 = new HashMap(); // 场景信息
}
