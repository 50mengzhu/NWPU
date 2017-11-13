package com.zyw.nwpu.tool;

//import HttpRequest.*;
//import java.io.*;
//import java.awt.image.*;
//import java.awt.geom.AffineTransform;
//import java.awt.color.ColorSpace;
//import java.awt.image.ConvolveOp;
//import java.awt.image.Kernel;
//import java.awt.image.BufferedImage;
//import javax.imageio.ImageIO;
//import java.awt.Toolkit;
//import java.awt.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class getMoney {
	public static String username;
	public static String password;
	public static String money;
	public static String people;
	public static String networkGUsed;
	public static String networkGToatle;
	public static String networkMUsed;
	public static String networkMToatle;
	public static String[] ip;
	public static String[] time;
	public static String flow;

	public getMoney(String name, String pass) {
		username = name;
		password = pass;
	}

	public static int[][][] pix = {
			{ { 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 0, 255, 255, 255, 255, },
					{ 255, 0, 0, 255, 0, 0, 255, 255, 255, },
					{ 255, 0, 255, 255, 255, 0, 255, 255, 255, },
					{ 0, 0, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 0, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 0, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 0, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 0, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 0, 255, 255, 255, 0, 0, 255, 255, },
					{ 255, 0, 255, 255, 255, 0, 255, 255, 255, },
					{ 255, 0, 0, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 0, 0, 0, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, }, },
			{ { 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 255, 255, 255, 255, 255, },
					{ 0, 0, 0, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 255, 255, 255, 255, 255, },
					{ 0, 0, 0, 0, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, }, },
			{ { 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 0, 0, 0, 0, 255, 255, 255, 255, },
					{ 0, 255, 255, 0, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 255, 255, 255, 255, },
					{ 255, 255, 255, 0, 0, 255, 255, 255, 255, },
					{ 255, 255, 255, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 255, 255, 255, 255, 255, 255, },
					{ 255, 0, 255, 255, 255, 255, 0, 255, 255, },
					{ 0, 0, 0, 0, 0, 0, 0, 255, 255, },
					{ 0, 0, 0, 0, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, }, },
			{ { 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 0, 0, 0, 0, 255, 255, 255, 255, },
					{ 0, 255, 255, 0, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 0, 0, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 0, 255, 255, 255, 255, },
					{ 255, 255, 255, 0, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 0, 255, 255, 0, 0, 255, 255, 255, 255, },
					{ 0, 0, 0, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, }, },
			{ { 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 0, 0, 0, 255, 255, 255, },
					{ 255, 255, 0, 255, 0, 0, 255, 255, 255, },
					{ 255, 0, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 0, 255, 255, 0, 0, 255, 255, 255, },
					{ 0, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 0, 0, 0, 0, 0, 0, 0, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, }, },
			{ { 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 0, 0, 255, 255, 255, },
					{ 255, 255, 0, 0, 0, 0, 255, 255, 255, },
					{ 255, 0, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 0, 0, 0, 255, 255, 255, 255, 255, },
					{ 0, 0, 0, 0, 0, 255, 255, 255, 255, },
					{ 255, 255, 255, 0, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 0, 255, 255, 255, },
					{ 0, 255, 255, 255, 0, 255, 255, 255, 255, },
					{ 0, 0, 0, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, }, },
			{ { 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 0, 255, 255, },
					{ 255, 255, 0, 0, 0, 255, 255, 255, 255, },
					{ 255, 0, 0, 255, 255, 255, 255, 255, 255, },
					{ 0, 0, 255, 255, 255, 255, 255, 255, 255, },
					{ 0, 255, 0, 0, 0, 255, 255, 255, 255, },
					{ 0, 0, 255, 255, 0, 0, 255, 255, 255, },
					{ 0, 255, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 255, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 255, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 255, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 0, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 0, 0, 0, 0, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, }, },
			{ { 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 0, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 0, 0, 0, 0, 0, 0, 0, 255, },
					{ 255, 0, 0, 0, 0, 0, 0, 255, 255, },
					{ 0, 255, 255, 255, 255, 255, 0, 255, 255, },
					{ 255, 255, 255, 255, 255, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 255, 255, 255, 255, },
					{ 255, 255, 255, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 0, 255, 255, 255, 255, 255, },
					{ 255, 255, 0, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, }, },
			{ { 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 0, 0, 0, 0, 255, 255, 255, 255, },
					{ 0, 0, 255, 255, 255, 0, 255, 255, 255, },
					{ 0, 255, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 255, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 0, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 0, 0, 0, 255, 255, 255, 255, 255, },
					{ 255, 0, 0, 0, 0, 255, 255, 255, 255, },
					{ 0, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 0, 255, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 255, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 0, 255, 255, 255, 0, 255, 255, 255, },
					{ 255, 0, 0, 0, 0, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, }, },
			{ { 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 0, 0, 0, 0, 255, 255, 255, 255, },
					{ 0, 0, 255, 255, 0, 0, 255, 255, 255, },
					{ 0, 255, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 255, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 255, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 255, 255, 255, 255, 0, 0, 255, 255, },
					{ 0, 0, 255, 255, 255, 0, 0, 255, 255, },
					{ 255, 0, 0, 0, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 255, 0, 0, 255, 255, 255, },
					{ 255, 255, 255, 0, 0, 255, 255, 255, 255, },
					{ 255, 255, 0, 0, 255, 255, 255, 255, 255, },
					{ 0, 0, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, },
					{ 255, 255, 255, 255, 255, 255, 255, 255, 255, }, }, };

	public static Bitmap binary(Bitmap image) {// 将图片二值化
		int iw = image.getWidth();
		int ih = image.getHeight();

		for (int i = 0; i < iw; i++) {
			for (int j = 0; j < ih; j++) {
				int pixel = image.getPixel(i, j);
				int alpha = Color.alpha(pixel);
				int blue = Color.blue(pixel);
				int red = Color.red(pixel);
				int green = Color.green(pixel);

				if (red < 90 || green < 136 || blue < 50) {
					red = 0;
					green = 0;
					blue = 0;
				} else {
					red = 255;
					green = 255;
					blue = 255;
				}
				/*
				 * if(i % image.getWidth() == 0) tmp += '\n'; if(red == 255) tmp
				 * += "  "; else if(red == 0) tmp += "█";
				 */
				// 对图像进行二值化处理，Alpha值保持不变
				pixel = Color.argb(alpha, red, green, blue);
				image.setPixel(i, j, pixel);
			}
		}
		return image;
	}

	public static Bitmap cutImage(Bitmap image, int left, int right) {// 剪切图片
		int ih = image.getHeight();
		return Bitmap.createBitmap(image, left, 0, right - left, ih);
	}

	public static int compare(int[][] pixData, Bitmap image, int fuck) {// 获取异或对比值
		int iw = image.getWidth();
		int ih = image.getHeight();

		int tmp = 0;
		// int num = 0;
		for (int i = 0; i < iw; i++) {
			for (int j = 0; j < ih; j++) {
				int pixel = image.getPixel(i, j);
				// num = j * iw + i;
				if (Color.red(pixel) != pixData[j][i])
					tmp++;
				if (fuck == 3) {
					// System.out.println(cm.getRed(pixels[num]) + "   " +
					// pixData[j][i] + "    3");
				}
			}
		}
		return tmp;
	}

	public static int getNum(String p, String m) {// 获取匹配的个数
		Pattern pa = Pattern.compile(p);
		Matcher ma = pa.matcher(m); // 获取 matcher 对象
		int count = 0;
		while (ma.find())
			count++;
		return count;
	}

	public static String getVal(String p, String m) {// 获取匹配的唯一对象
		Pattern r = Pattern.compile(p);
		// System.out.println(p + '\n' + m);
		// 现在创建 matcher 对象
		Matcher ma = r.matcher(m);
		if (ma.find()) {
			// System.out.println("Found value: " + ma.group(1) );
			return ma.group(1);
		} else {
			System.out.println("NO MATCH");
			return "NO";
		}
	}

	public static String[] getAll(String p, String m) {// 获取匹配的所有对象的列表
		int i = 0;
		String[] content = new String[1000];
		// System.out.println(m);
		Pattern pattern = Pattern.compile(p);
		Matcher ma = pattern.matcher(m);
		while (ma.find()) {
			content[i] = ma.group(1);
			i = i + 1;
		}
		int a = 0;
		while (a < content.length && content[a] != null) {
			// System.out.println(content[a]);
			a++;
		}

		return content;
	}

	public static String recognize(Bitmap image) {// 识别验证码
		int tmp = 1000;
		int num = -1;
		int[][] pixData;
		for (int i = 0; i < 10; i++) {
			pixData = pix[i];
			if (compare(pixData, image, i) < tmp) {
				num = i;
				tmp = compare(pixData, image, i);
			}
			// System.out.print(tmp);
		}
		return num + "";
	}

	public static Bitmap getImage(String u, String cookie) { // 获取网络图片
		try {
			URL url = new URL(u);
			URLConnection con = url.openConnection();
			con.setRequestProperty("Cookie", cookie);
			con.setConnectTimeout(5 * 1000);
			InputStream is = con.getInputStream();
			int count = is.available();
			byte[] b = new byte[count];
			is.read(b);
			is.close();
			Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
			return bitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getCode(String cookie) {// 获取验证码并返回
		Bitmap img, imgTemp;
		img = getImage(
				"http://self.nwpu.edu.cn:8080/selfservice/common/web/verifycode.jsp",
				cookie);
		int i = 0;
		int start;
		String code = "";
		for (; i < 4; i++) {
			start = 7 + i * 13;
			imgTemp = cutImage(img, start, start + 9);
			imgTemp = binary(imgTemp);
			code += recognize(imgTemp);
		}
		return code;
	}

	public static boolean reg(String cookie, String code) {// 注册该cookie使其能进入锐捷
		String res = "";
		int mistake = 1;
		while (mistake == 1) {
			res = HttpRequest
					.sendPost(
							"http://self.nwpu.edu.cn:8080/selfservice/module/scgroup/web/login_judge.jsf",
							"password=" + password + "&name=" + username
									+ "&verify=" + code, cookie, false);
			if (getNum("verfiyError", res) < 1)
				mistake = 0;
		}
		if (getNum("errorMsg", res) >= 1)
			return false;
		return true;
	}

	public static String getFlow(String networkGUsed, String networkGToatle,
			String networkMUsed, String networkMToatle) { // 计算还剩多少流量
		int networkGUsedInt = Integer.parseInt(networkGUsed);
		int networkGToatleInt = Integer.parseInt(networkGToatle);
		float networkMUsedFloat = Float.parseFloat(networkMUsed);
		float networkMToatleFloat = Float.parseFloat(networkMToatle);
		int GB = networkGToatleInt - networkGUsedInt;
		float MB = networkMToatleFloat - networkMUsedFloat;

		if (GB > 1) {
			GB = GB - 1;
			MB = MB + 1024;
			return "上次使用后还剩 " + GB + " GB" + " " + MB + " MB";
		} else {
			GB = GB * -1;
			MB = MB * -1;
			return "上次使用后超出 " + GB + " GB" + " " + MB + " MB";
		}
	}

	public static boolean getAllFlow() {
		username = "2013302395";
		password = "123654789";
		String cookie = HttpRequest
				.getCookie("http://self.nwpu.edu.cn:8080/selfservice/common/web/verifycode.jsp");
		String code = getCode(cookie);
		if (!reg(cookie, code)) {
			return false;
		}
		String res = HttpRequest
				.sendGet(
						"http://self.nwpu.edu.cn:8080/selfservice/module/webcontent/web/index_self.jsf?",
						cookie);
		res = HttpRequest
				.sendGet(
						"http://self.nwpu.edu.cn:8080/selfservice/module/webcontent/web/content_self.jsf",
						cookie);
		try {
			money = getVal(
					"offileForm:currentAccountFeeValue\">([0-9.]*)</span>", res);
			people = getVal("<font color=\"red\">([0-9]*)</font>", res);
			networkGUsed = getVal("国内下行流量:([0-9]*) GB", res);
			networkGToatle = getVal("/([0-9]*) GB", res);
			networkMUsed = getVal("([0-9.]*) MB /", res);
			networkMToatle = getVal("([0-9.]*) MB </span>", res);
			ip = getAll("在线IP地址：([0-9.]*)</td>", res);
			time = getAll("上线时间：([0-9:. -]*)</td>", res);
			if (networkGUsed == "NO")
				networkGUsed = "0";
			flow = getFlow(networkGUsed, networkGToatle, networkMUsed,
					networkMToatle);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
}