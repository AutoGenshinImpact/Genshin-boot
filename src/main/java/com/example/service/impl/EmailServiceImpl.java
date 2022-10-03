package com.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.controller.exception.NotExistInMysqlException;
import com.example.dao.MiJingMapper;
import com.example.entity.constant.MiJingList;
import com.example.entity.constant.ThreadDetails;
import com.example.entity.data.MiJingDetail;
import com.example.service.EmailService;
import com.example.service.util.RedisTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    String sendEmail;
    @Resource
    MiJingMapper miJingMapper;
    @Resource
    RedisTools<String> redisTools;

    private static final String UNSUBSCRIBE_EMAIL_TOKEN = "MiJing:email:unsubscribeEmail:";


    @Override
    public boolean sendEmail(String mail) {
        String username = ThreadDetails.getUsername();
        MiJingDetail miJingDetail = miJingMapper.selectList(username).orElseThrow(() -> new NotExistInMysqlException("该用户没有储存秘境列表"));
        String list = miJingDetail.getList();
        String numberList = miJingDetail.getNumberList();
        List<Integer> toList = JSON.parseArray(list, Integer.class);
        List<Integer> toNumberList = JSON.parseArray(numberList, Integer.class);
        List<Object> nameList = toList.stream().map(MiJingList.MiJingMap::get).collect(Collectors.toList());


        ArrayList<ArrayList<Object>> allColumnArrayList = new ArrayList<ArrayList<Object>>();
        nameList.forEach(getIndex((item, index) -> {
            ArrayList<Object> arr = new ArrayList();
            arr.add(nameList.get(index));
            arr.add(toNumberList.get(index));
            arr.add(toList.get(index));
            allColumnArrayList.add(arr);
        }));
        //获取任务列表

//
        try {
            //注意这里使用的是MimeMessage
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sendEmail);
            helper.setTo(mail);
            helper.setSubject("[GenShin脚本]您的秘境任务已经完成，请注意查看");
            //第二个参数：格式是否为html
            helper.setText(getMessage(mail, allColumnArrayList), true);
            javaMailSender.send(message);
            return true;
        } catch (MessagingException e) {
            log.error("发送邮件时发生异常！", e);
            return false;
        }
    }

    @Override
    public boolean unsubscribeEmail(String mail) throws Exception {
        try {
            redisTools.setToRedis(UNSUBSCRIBE_EMAIL_TOKEN + mail, "true", 30, RedisTools.DAYS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("退订失败!");
        }
    }

    @Override
    public boolean renewEmail(String mail) throws Exception {
        try {
            redisTools.deleteFromRedis(UNSUBSCRIBE_EMAIL_TOKEN+mail);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("续订失败!");
        }
    }

    private String getMessage(String email, ArrayList<ArrayList<Object>> list) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String date = format.format(new Date());
        List<String> columnList = list.stream().map(childList -> {
            return getColumn((String) childList.get(0), (Integer) childList.get(1), (Integer) childList.get(2));
        }).collect(Collectors.toList());
        final String[] allColumn = {""};
        columnList.forEach(column -> {
            allColumn[0] = allColumn[0].concat(column);
        });
        return "<div>\n" +
                "    <div style=\"font-family: &quot;lucida Grande&quot;, Verdana, &quot;Microsoft YaHei&quot;; background: rgb(94, 123, 254); margin: 0px auto; max-width: 600px;\">\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\"\n" +
                "               style=\"background-image: initial; background-position: initial; background-size: initial; background-repeat: initial; background-attachment: initial; background-origin: initial; background-clip: initial; width: 600px;\">\n" +
                "            <tbody>\n" +
                "            <tr>\n" +
                "                <td style=\"font-size: 0px; -webkit-font-smoothing: subpixel-antialiased; direction: ltr; padding: 0px; text-align: center;\">\n" +
                "                    <div class=\"mj-column-per-100 mj-outlook-group-fix\"\n" +
                "                         style=\"text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 600px;\">\n" +
                "                        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n" +
                "                            <tbody>\n" +
                "                            <tr>\n" +
                "                                <td style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; vertical-align: top; padding: 3px 5px;\">\n" +
                "                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n" +
                "                                        <tbody>\n" +
                "                                        <tr>\n" +
                "                                            <td align=\"left\"\n" +
                "                                                style=\"font-size: 0px; -webkit-font-smoothing: subpixel-antialiased; padding: 3px 5px; word-break: break-word;\">\n" +
                "                                                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\"\n" +
                "                                                       style=\"border-collapse: collapse; border-spacing: 0px;\">\n" +
                "                                                    <tbody>\n" +
                "                                                    <tr>\n" +
                "                                                        <td style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; width: 100px;\">\n" +
                "                                                            <table><tr>\n" +
                "                                                                <td><img height=\"auto\"\n" +
                "                                                                         src=\"https://img.gejiba.com/images/92476aa90946e4dedc4f650f53f27d77.png\"\n" +
                "                                                                         width=\"100\"\n" +
                "                                                                         style=\"border: 0px; vertical-align: middle; display: block; outline: none; height: auto; width: 150px;transform: translateX(-60%); font-size: 16px;\">\n" +
                "                                                                </td>\n" +
                "                                                                <td><h4 style=\"transform: translateX(-540%);margin: 0;margin-top:6px;font-size: 19px;font-weight: bold;color: #d3d4d6;font-family: 'Adobe Caslon Pro'\">G</h4> </td>\n" +
                "                                                                <td><h4 style=\"transform: translateX(-1040%);margin: 0;margin-top:6px;font-size: 19px;font-weight: bold;color: #d3d4d6;font-family: 'Adobe Caslon Pro'\">e</h4> </td>\n" +
                "                                                                <td><h4 style=\"transform: translateX(-840%);margin: 0;margin-top:6px;font-size: 19px;font-weight: bold;color: #d3d4d6;font-family: 'Adobe Caslon Pro'\">n</h4> </td>\n" +
                "                                                                <td><h4 style=\"transform: translateX(-840%);margin: 0;margin-top:6px;font-size: 19px;font-weight: bold;color: #d3d4d6;font-family: 'Adobe Caslon Pro'\">S</h4> </td>\n" +
                "                                                                <td><h4 style=\"transform: translateX(-880%);margin: 0;margin-top:6px;font-size: 19px;font-weight: bold;color: #d3d4d6;font-family: 'Adobe Caslon Pro'\">h</h4> </td>\n" +
                "                                                                <td><h4 style=\"transform: translateX(-1730%);margin: 0;margin-top:6px;font-size: 19px;font-weight: bold;color: #d3d4d6;font-family: 'Adobe Caslon Pro'\">i</h4> </td>\n" +
                "                                                                <td><h4 style=\"transform: translateX(-910%);margin: 0;margin-top:6px;font-size: 19px;font-weight: bold;color: #d3d4d6;font-family: 'Adobe Caslon Pro'\">n</h4> </td>\n" +
                "                                                            </tr></table>\n" +
                "\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    </tbody>\n" +
                "                                                </table>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                        </tbody>\n" +
                "                                    </table>\n" +
                "                                </td>\n" +
                "                            </tr>\n" +
                "                            </tbody>\n" +
                "                        </table>\n" +
                "                    </div>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            </tbody>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "    <div style=\"font-family: &quot;lucida Grande&quot;, Verdana, &quot;Microsoft YaHei&quot;; background-image: initial; background-position: initial; background-size: initial; background-repeat: initial; background-attachment: initial; background-origin: initial; background-clip: initial; margin: 0px auto; max-width: 600px;\">\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\"\n" +
                "               style=\"background-image: initial; background-position: initial; background-size: initial; background-repeat: initial; background-attachment: initial; background-origin: initial; background-clip: initial; width: 600px;\">\n" +
                "            <tbody>\n" +
                "            <tr>\n" +
                "                <td style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; direction: ltr; padding: 10px; text-align: center;\">\n" +
                "                    <div class=\"mj-column-per-100 mj-outlook-group-fix\"\n" +
                "                         style=\"text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 580px;\">\n" +
                "                        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n" +
                "                            <tbody>\n" +
                "                            <tr>\n" +
                "                                <td style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; vertical-align: top; padding: 3px 5px;\">\n" +
                "                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n" +
                "                                        <tbody>\n" +
                "                                        <tr>\n" +
                "                                            <td align=\"left\"\n" +
                "                                                style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; padding: 3px 5px 0px; word-break: break-word;\">\n" +
                "                                                <div><h3 style=\"font-weight: normal; margin: 0px;\"><font size=\"4\"\n" +
                "                                                                                                         style=\"line-height: 30.6px;\"><font\n" +
                "                                                        color=\"#3c5061\"\n" +
                "                                                        style=\"line-height: 30.6px;\">GenShin脚本使用者&nbsp;</font><font\n" +
                "                                                        color=\"#ff9900\"\n" +
                "                                                        style=\"line-height: 30.6px;\">" + email.replace("@qq.com","") + "</font><font\n" +
                "                                                        color=\"#3c5061\" style=\"line-height: 30.6px;\">，您好：</font></font>\n" +
                "                                                </h3></div>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                        <tr>\n" +
                "                                            <td align=\"left\"\n" +
                "                                                style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; padding: 0px 5px 3px; word-break: break-word;\">\n" +
                "                                                <p style=\"line-height: 23.8px;\"><font size=\"2\"\n" +
                "                                                                                      style=\"line-height: 22.1px;\"><font\n" +
                "                                                        color=\"#3c5061\" style=\"line-height: 22.1px;\">您在</font><font\n" +
                "                                                        color=\"#008080\"\n" +
                "                                                        style=\"line-height: 22.1px;\">" + date + "</font><font\n" +
                "                                                        color=\"#3c5061\"\n" +
                "                                                        style=\"line-height: 22.1px;\">，完成了一次秘境脚本的执行：</font></font></p>\n" +
                "                                                <p style=\"line-height: 23.8px;\"><font size=\"2\" color=\"#3366ff\"\n" +
                "                                                                                      style=\"line-height: 22.1px;\"><b>脚本内容：</b></font>\n" +
                "                                                </p></td>\n" +
                "                                        </tr>\n" +
                "                                        <tr>\n" +
                "                                            <td align=\"left\"\n" +
                "                                                style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; padding: 3px 5px; word-break: break-word;\">\n" +
                "                                                <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\"\n" +
                "                                                       style=\"color: rgb(60, 80, 97); font-size: 14px; line-height: 24px; table-layout: auto; width: 560px; border: none;\">\n" +
                "                                                    <tbody>\n" +
                "                                                    <tr style=\"transform: translateX(-70%);border-bottom: 1px solid rgb(236, 237, 238);\">\n" +
                "                                                        <th>秘境名称</th>\n" +
                "                                                        <th>数量</th>\n" +
                "                                                        <th>脚本编号</th>\n" +
                "\n" +
                "                                                    </tr>\n" +
                allColumn[0] +
                "                                                    </tbody>\n" +
                "                                                </table>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                        </tbody>\n" +
                "                                    </table>\n" +
                "                                </td>\n" +
                "                            </tr>\n" +
                "                            </tbody>\n" +
                "                        </table>\n" +
                "                    </div>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            </tbody>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "    <div style=\"font-family: &quot;lucida Grande&quot;, Verdana, &quot;Microsoft YaHei&quot;; background-image: initial; background-position: initial; background-size: initial; background-repeat: initial; background-attachment: initial; background-origin: initial; background-clip: initial; margin: 0px auto; max-width: 600px;\">\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\"\n" +
                "               style=\"background-image: initial; background-position: initial; background-size: initial; background-repeat: initial; background-attachment: initial; background-origin: initial; background-clip: initial; width: 600px;\">\n" +
                "            <tbody>\n" +
                "            <tr>\n" +
                "                <td style=\"font-size: 0px; -webkit-font-smoothing: subpixel-antialiased; direction: ltr; padding: 10px; text-align: center;\">\n" +
                "                    <div class=\"mj-column-per-100 mj-outlook-group-fix\"\n" +
                "                         style=\"text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 580px;\">\n" +
                "                        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n" +
                "                            <tbody>\n" +
                "                            <tr>\n" +
                "                                <td style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; vertical-align: top; padding: 3px 5px;\">\n" +
                "                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n" +
                "                                        <tbody>\n" +
                "                                        <tr>\n" +
                "                                            <td align=\"center\"\n" +
                "                                                style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; padding: 3px 5px; word-break: break-word;\">\n" +
                "                                                <p style=\"line-height: 20.4px; border-top: 1px solid rgb(223, 227, 232); margin: 0px auto; width: 560px;\"></p>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                        </tbody>\n" +
                "                                    </table>\n" +
                "                                </td>\n" +
                "                            </tr>\n" +
                "                            </tbody>\n" +
                "                        </table>\n" +
                "                    </div>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            </tbody>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "    <div style=\"font-family: &quot;lucida Grande&quot;, Verdana, &quot;Microsoft YaHei&quot;; background-image: initial; background-position: initial; background-size: initial; background-repeat: initial; background-attachment: initial; background-origin: initial; background-clip: initial; margin: 0px auto; max-width: 600px;\">\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\"\n" +
                "               style=\"background-image: initial; background-position: initial; background-size: initial; background-repeat: initial; background-attachment: initial; background-origin: initial; background-clip: initial; width: 600px;\">\n" +
                "            <tbody>\n" +
                "            <tr>\n" +
                "                <td style=\"font-size: 0px; -webkit-font-smoothing: subpixel-antialiased; direction: ltr; padding: 10px; text-align: center;\">\n" +
                "                    <div class=\"mj-column-per-100 mj-outlook-group-fix\"\n" +
                "                         style=\"text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 580px;\">\n" +
                "                        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n" +
                "                            <tbody>\n" +
                "                            <tr>\n" +
                "                                <td style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; vertical-align: top; padding: 3px 5px;\">\n" +
                "                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n" +
                "                                        <tbody>\n" +
                "                                        <tr>\n" +
                "                                            <td align=\"left\"\n" +
                "                                                style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; padding: 3px 5px; word-break: break-word;\">\n" +
                "                                                <p style=\"line-height: 23.8px;\"><font size=\"2\"\n" +
                "                                                                                      style=\"line-height: 22.1px;\">您好，因为网站可能休眠无法及时提醒，所以冒昧通过邮件提醒您。如果您觉得GenShin脚本的信息对您没有帮助，请<a\n" +
                "                                                        href=\"https://d1izb3sh3.neiwangyun.net/unsubscribe.html\"\n" +
                "                                                        rel=\"noopener\" target=\"_blank\"\n" +
                "                                                        style=\"outline: none; text-decoration-line: none; cursor: pointer; color: rgb(77, 93, 44);\">点击取消退订</a>，我们会标记为暂不关心，后续不会再次提示您。</font>\n" +
                "                                                </p></td>\n" +
                "                                        </tr>\n" +
                "                                        <tr>\n" +
                "                                            <td align=\"left\"\n" +
                "                                                style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; padding: 3px 5px 10px; word-break: break-word;\">\n" +
                "                                                <div><a href=\"https://d1izb3sh3.neiwangyun.net/\" size=\"2\" style=\"line-height: 22.1px;\">GenShin脚本网站</a>\n" +
                "                                                </div>\n" +
                "                                                <div><a href=\"https://github.com/AutoGenshinImpact/Genshin-boot\" size=\"2\" style=\"line-height: 22.1px;\">联系我们：GitHub</a>\n" +
                "                                                </div>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                        </tbody>\n" +
                "                                    </table>\n" +
                "                                </td>\n" +
                "                            </tr>\n" +
                "                            </tbody>\n" +
                "                        </table>\n" +
                "                    </div>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            </tbody>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "    <div style=\"font-family: &quot;lucida Grande&quot;, Verdana, &quot;Microsoft YaHei&quot;; background-image: initial; background-position: initial; background-size: initial; background-repeat: initial; background-attachment: initial; background-origin: initial; background-clip: initial; margin: 0px auto; max-width: 600px;\">\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\"\n" +
                "               style=\"background-image: initial; background-position: initial; background-size: initial; background-repeat: initial; background-attachment: initial; background-origin: initial; background-clip: initial; width: 600px;\">\n" +
                "            <tbody>\n" +
                "            <tr>\n" +
                "                <td style=\"font-size: 0px; -webkit-font-smoothing: subpixel-antialiased; direction: ltr; padding: 30px 10px 10px; text-align: center;\">\n" +
                "                    <div class=\"mj-column-per-100 mj-outlook-group-fix\"\n" +
                "                         style=\"text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 580px;\">\n" +
                "                        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n" +
                "                            <tbody>\n" +
                "                            <tr>\n" +
                "                                <td style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; vertical-align: top; padding: 3px 5px;\">\n" +
                "                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n" +
                "                                        <tbody>\n" +
                "                                        <tr>\n" +
                "                                            <td align=\"left\"\n" +
                "                                                style=\"font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; padding: 3px 5px; word-break: break-word;\">\n" +
                "                                                <p style=\"line-height: 23.8px;\"><font size=\"4\"\n" +
                "                                                                                      style=\"line-height: 30.6px;\">GenShin脚本敬上</font>\n" +
                "                                                </p>\n" +
                "                                                <p style=\"line-height: 23.8px;\"><font size=\"4\"\n" +
                "                                                                                      style=\"line-height: 30.6px;\">祝您工作顺利，生活愉快</font>\n" +
                "                                                </p></td>\n" +
                "                                        </tr>\n" +
                "                                        </tbody>\n" +
                "                                    </table>\n" +
                "                                </td>\n" +
                "                            </tr>\n" +
                "                            </tbody>\n" +
                "                        </table>\n" +
                "                    </div>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            </tbody>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "</div>\n";
    }

    private String getColumn(String name, int number, int index) {
        int translate = 0;
        if (name.length() == 3) {
            translate = 44;
        } else if (name.length() == 4) {
            translate = 43;
        } else if (name.length() == 6) {
            translate = 38;
        }
        return "<tr>\n" +
                "                                                        <td width=\"200\"\n" +
                "                                                            style=\"transform: translateX(" + translate + "%);font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; word-break: break-all;\">\n" +
                "                                                            " + name + "\n" +
                "                                                        </td>\n" +
                "                                                        <td width=\"80\"\n" +
                "                                                            style=\"transform: translateX(41%);font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; word-break: break-all;\">\n" +
                "                                                            " + number + "\n" +
                "                                                        </td>\n" +
                "                                                        <td width=\"100\"\n" +
                "                                                            style=\"transform: translateX(39%);font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; word-break: break-all;\">\n" +
                "                                                            " + index + "\n" +
                "                                                        </td>\n" +
                "                                                    </tr>";
    }

    public static <T> Consumer<T> getIndex(BiConsumer<T, Integer> consumer) {
        class IndexObject {
            int index;
        }
        IndexObject indexObject = new IndexObject();
        return i -> {
            consumer.accept(i, indexObject.index++);
        };
    }
}

