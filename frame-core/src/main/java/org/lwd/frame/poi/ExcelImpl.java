package org.lwd.frame.poi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lwd
 */
@Component("frame.poi.excel")
public class ExcelImpl implements Excel {
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;

    @Override
    public void write(String[] titles, String[] names, JSONArray array, OutputStream outputStream) {
        try {
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            Row row = sheet.createRow(0);
            for (int i = 0; i < titles.length; i++)
                row.createCell(i).setCellValue(titles[i]);
            for (int i = 0, size = array.size(); i < size; i++) {
                JSONObject object = array.getJSONObject(i);
                row = sheet.createRow(i + 1);
                for (int j = 0; j < names.length; j++)
                    row.createCell(j).setCellValue(converter.toString(object.get(names[j])));
            }
            workbook.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            logger.warn(e, "输出Excel文档时发生异常！");
        }
    }

    @Override
    public boolean sheetExist(String fileDir, String sheetName) {
        if (fileDir.contains("xlsx")) {
            return sheetXlsxExist(fileDir, sheetName);
        }
        return sheetXlsExist(fileDir, sheetName);
    }

    @Override
    public boolean sheetXlsExist(String fileDir, String sheetName) {
        boolean flag = false;
        File file = new File(fileDir);
        if (file.exists()) {    //文件存在
            //创建workbook
            try {
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(file));
                //添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
                HSSFSheet sheet = hssfWorkbook.getSheet(sheetName);
                if (sheet != null)
                    flag = true;
            } catch (Exception e) {
                logger.error(e, "创建workbook失败！");
            }

        } else {    //文件不存在
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean sheetXlsxExist(String fileDir, String sheetName) {
        boolean flag = false;
        File file = new File(fileDir);
        if (file.exists()) {    //文件存在
            //创建workbook
            try {
                XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(file));
                //添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
                XSSFSheet sheet = xssfWorkbook.getSheet(sheetName);
                if (sheet != null)
                    flag = true;
            } catch (Exception e) {
                logger.error(e, "创建workbook失败！");
            }

        } else {    //文件不存在
            flag = false;
        }
        return flag;
    }

    @Override
    public void createExcel(String fileDir, String sheetName, String[] titleRow) {
        //创建workbook
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        //添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
        Sheet sheet1 = hssfWorkbook.createSheet(sheetName);
        //新建文件
        FileOutputStream out = null;
        try {
            //添加表头
            Row row = hssfWorkbook.getSheet(sheetName).createRow(0);    //创建第一行
            for (int i = 0; i < titleRow.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(titleRow[i]);
            }

            out = new FileOutputStream(fileDir);
            hssfWorkbook.write(out);
        } catch (Exception e) {
            logger.error(e, "写入文件失败！");
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                logger.error(e, "关闭文件流失败！");
            }
        }
    }

    @Override
    public void writeToExcel(String fileDir, String sheetName, Object object) {
        //创建workbook
        File file = new File(fileDir);
        HSSFWorkbook hssfWorkbook = null;
        try {
            hssfWorkbook = new HSSFWorkbook(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.error(e, "创建workbook失败");
        } catch (IOException e) {
            logger.error(e, "输入输出流有误！");
        }
        //流
        FileOutputStream out = null;
        HSSFSheet sheet = hssfWorkbook.getSheet(sheetName);
        // 获取表格的总行数
        int rowCount = sheet.getLastRowNum() + 1; // 需要加一
        // 获取表头的列数
        int columnCount = sheet.getRow(0).getLastCellNum();
        try {
            Row row = sheet.createRow(rowCount);     //最新要添加的一行
            //通过反射获得object的字段,对应表头插入
            // 获取该对象的class对象
            Class class_ = object.getClass();
            // 获得表头行对象
            HSSFRow titleRow = sheet.getRow(0);
            if (titleRow != null) {
                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {  //遍历表头
                    String title = titleRow.getCell(columnIndex).toString().trim().toString().trim();
                    String UTitle = Character.toUpperCase(title.charAt(0)) + title.substring(1, title.length()); // 使其首字母大写;
                    String methodName = "get" + UTitle;
                    Method method = class_.getDeclaredMethod(methodName); // 设置要执行的方法
                    String data = method.invoke(object).toString(); // 执行该get方法,即要插入的数据
                    Cell cell = row.createCell(columnIndex);
                    cell.setCellValue(data);
                }
            }

            out = new FileOutputStream(fileDir);
            hssfWorkbook.write(out);
        } catch (Exception e) {
            logger.error(e, "往excel中写入失败！");
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                logger.error(e, "往excel中写入，关闭输入流失败！");
            }
        }
    }

    @Override
    public List readFromXlsExcel(String fileDir, String sheetName, Object object) throws IOException {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(new File(fileDir)));
        List result = new ArrayList();
        // 获取该对象的class对象
        Class class_ = object.getClass();
        // 获得该类的所有属性
        Field[] fields = class_.getDeclaredFields();
        // 读取excel数据
        // 获得指定的excel表
        HSSFSheet sheet = hssfWorkbook.getSheet(sheetName);
        // 获取表格的总行数
        int rowCount = sheet.getLastRowNum() + 1; // 需要加一
//        System.out.println("rowCount:" + rowCount);
        if (rowCount < 1) {
            return result;
        }
        // 获取表头的列数
        int columnCount = sheet.getRow(0).getLastCellNum();
        // 读取表头信息,确定需要用的方法名---set方法
        // 用于存储方法名
        String[] methodNames = new String[columnCount]; // 表头列数即为需要的set方法个数
        // 用于存储属性类型
        String[] fieldTypes = new String[columnCount];
        // 获得表头行对象
//        HSSFRow titleRow = sheet.getRow(0);
        // 遍历
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) { // 遍历表头列
            String name = fields[columnIndex].getName();
            methodNames[columnIndex] = MessageFormat.format("{0}{1}",
                    "set",
                    Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length()));
            fieldTypes[columnIndex] = fields[columnIndex].getType().getName(); // 将属性类型放到数组中
        }
        // 逐行读取数据 从1开始 忽略表头
        for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
            // 获得行对象
            HSSFRow row = sheet.getRow(rowIndex);
            if (row != null) {
                Object obj = null;
                // 实例化该泛型类的对象一个对象
                try {
                    obj = class_.newInstance();
                } catch (Exception e1) {
                    logger.error(e1, "实例化该泛型类的对象一个对象失败！");
                }

                // 获得本行中各单元格中的数据
                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                    String data = row.getCell(columnIndex).toString();
                    // 获取要调用方法的方法名
                    String methodName = methodNames[columnIndex];
                    Method method = null;
                    try {
                        // 这部分可自己扩展
                        if (fieldTypes[columnIndex].equals("java.lang.String")) {
                            method = class_.getDeclaredMethod(methodName,
                                    String.class); // 设置要执行的方法--set方法参数为String
                            method.invoke(obj, data); // 执行该方法
                        } else if (fieldTypes[columnIndex].equals("int")) {
                            method = class_.getDeclaredMethod(methodName,
                                    int.class); // 设置要执行的方法--set方法参数为int
                            double data_double = Double.parseDouble(data);
                            int data_int = (int) data_double;
                            method.invoke(obj, data_int); // 执行该方法
                        }
                    } catch (Exception e) {
                        logger.error(e, "读取文件失败！");
                    }
                }
                result.add(obj);
            }
        }
        return result;
    }

    @Override
    public List readFromXlsxExcel(String fileDir, String sheetName, Object object) throws IOException {
        //创建workbook
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(new File(fileDir)));
        List result = new ArrayList();
        Class class_ = object.getClass();
        Field[] fields = class_.getDeclaredFields();

        XSSFSheet sheet = xssfWorkbook.getSheet(sheetName);
        int rowCount = sheet.getLastRowNum() + 1; // 需要加一
        if (rowCount < 1) {
            return result;
        }
        int columnCount = sheet.getRow(0).getLastCellNum();
        String[] methodNames = new String[columnCount]; // 表头列数即为需要的set方法个数
        String[] fieldTypes = new String[columnCount];
//        XSSFRow titleRow = sheet.getRow(0);
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) { // 遍历表头列
            String name = fields[columnIndex].getName();
            methodNames[columnIndex] = MessageFormat.format("{0}{1}",
                    "set",
                    Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length()));
            fieldTypes[columnIndex] = fields[columnIndex].getType().getName(); // 将属性类型放到数组中

        }
        for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
            XSSFRow xrow = sheet.getRow(rowIndex);
            if (xrow != null) {
                Object xobj = null;
                try {
                    xobj = class_.newInstance();
                } catch (Exception e1) {
                    logger.error(e1, "实例化该泛型类的对象一个对象失败！");
                }

                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                    String data = xrow.getCell(columnIndex).toString();
                    String methodName = methodNames[columnIndex];
                    Method method = null;
                    try {
                        if (fieldTypes[columnIndex].equals("java.lang.String")) {
                            method = class_.getDeclaredMethod(methodName,
                                    String.class); // 设置要执行的方法--set方法参数为String
                            method.invoke(xobj, data); // 执行该方法
                        } else if (fieldTypes[columnIndex].equals("int")) {
                            method = class_.getDeclaredMethod(methodName,
                                    int.class); // 设置要执行的方法--set方法参数为int
                            double data_double = Double.parseDouble(data);
                            int data_int = (int) data_double;
                            method.invoke(xobj, data_int); // 执行该方法
                        }
                    } catch (Exception e) {
                        logger.error(e, "读取文件失败！");
                    }
                }
                result.add(xobj);
            }
        }
        return result;
    }

    @Override
    public List readFromExcel(String fileDir, String sheetName, Object object) {
        List result = new ArrayList();
        try {
            if (fileDir.contains("xlsx")) {
                result = readFromXlsxExcel(fileDir, sheetName, object);
            }else {
                result = readFromXlsExcel(fileDir, sheetName, object);
            }
        } catch (IOException e) {
            logger.error(e, "读取失败！");
        }
        return result;
    }
}
