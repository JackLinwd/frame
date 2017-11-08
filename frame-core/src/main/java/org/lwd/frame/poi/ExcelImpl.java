package org.lwd.frame.poi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;

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
}
