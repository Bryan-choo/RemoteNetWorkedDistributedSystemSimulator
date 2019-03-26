package com.coursework.evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WriteExcel {
    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";
    
    public static void main(String[] args) {
        
        Map<String, String> dataMap=new HashMap<String, String>();
        dataMap.put("BankName", "BankName");
        dataMap.put("Addr", "Addr");
        dataMap.put("Phone", "Phone");
        List<Map> list=new ArrayList<Map>();
        list.add(dataMap);
//        writeExcel(list, 12, "D:/writeExcel.xlsx");
        
    }
    
    public static void createExcel(String fileDir,String sheetName) throws Exception{  
        //����workbook  
    	HSSFWorkbook workbook = new HSSFWorkbook();  
        //���Worksheet�������sheetʱ���ɵ�xls�ļ���ʱ�ᱨ��)  
        HSSFSheet sheet1 = workbook.createSheet(sheetName);    
        //�½��ļ�  
        FileOutputStream out = null;  
        try {  
            //��ӱ�ͷ  
//            HSSFRow row = workbook.getSheet(sheetName).createRow(0);    //������һ��    
            Row firstrow = workbook.getSheet(sheetName).createRow(0);
            firstrow.createCell(0).setCellValue("numProcessors");
            firstrow.createCell(1).setCellValue("minHSTime");
            firstrow.createCell(2).setCellValue("minLCRTime");
            firstrow.createCell(3).setCellValue("maxHSTime");
            firstrow.createCell(4).setCellValue("maxLCRTime");
            firstrow.createCell(5).setCellValue("averageHSTime");
            firstrow.createCell(6).setCellValue("averageLCRTime");
            firstrow.createCell(7).setCellValue("minHSMessage");
            firstrow.createCell(8).setCellValue("minLCRMessage");
            firstrow.createCell(9).setCellValue("maxHSMessage");
            firstrow.createCell(10).setCellValue("minLCRMessage");
            firstrow.createCell(11).setCellValue("averageHSMessage");
            firstrow.createCell(12).setCellValue("averageLCRMessage");
            firstrow.createCell(13).setCellValue("averageHSRounds");
            firstrow.createCell(14).setCellValue("averageLCRRounds");
            out = new FileOutputStream(fileDir);  
            workbook.write(out);  
        } catch (Exception e) {  
            throw e;
        } finally {    
            try {    
                out.close();    
            } catch (IOException e) {    
                e.printStackTrace();  
            }    
        }    
    }  

    public static void writeExcel(HashMap<String, List<PerformanceEntry>> performance, int cloumnCount,String finalXlsxPath){
        OutputStream out = null;
        
        List<PerformanceEntry> hsPerformance = performance.get("HSPerformance");
        List<PerformanceEntry> lcrPerformance = performance.get("LCRPerformance");
        
        try {
            // ��ȡ������
            int columnNumCount = cloumnCount;
            // ��ȡExcel�ĵ�
            File finalXlsxFile = new File(finalXlsxPath);
            if (!finalXlsxFile.exists()) {
            	WriteExcel.createExcel(finalXlsxPath, "performance of HS and LCR");
            }
            Workbook workBook = getWorkbok(finalXlsxFile);
            
            org.apache.poi.ss.usermodel.Sheet sheet = workBook.getSheetAt(0);
            int rowNumber = sheet.getLastRowNum();    // ��һ�д�0��ʼ��
            for (int i = 1; i <= rowNumber; i++) {
                Row row = sheet.getRow(i);
                sheet.removeRow(row);
            }

            out =  new FileOutputStream(finalXlsxPath);
            workBook.write(out);
           
            
            for (int j = 0; j < hsPerformance.size(); j++) {
                Row row = sheet.createRow(j + 1);
                PerformanceEntry hs = hsPerformance.get(j);
                PerformanceEntry lcr = lcrPerformance.get(j);
                
                Cell processors = row.createCell(0);
                Cell minHSTime = row.createCell(1);
                Cell minLCRTime = row.createCell(2);
                Cell maxHSTime = row.createCell(3);
                Cell maxLCRTime = row.createCell(4);
                Cell averageHSTime = row.createCell(5);
                Cell averageLCRTime = row.createCell(6);
                
                Cell minHSMessage = row.createCell(7);
                Cell minLCRMessage = row.createCell(8);
                Cell maxHSMessage = row.createCell(9);
                Cell maxLCRMessage = row.createCell(10);
                Cell averageHSMessage = row.createCell(11);
                Cell averageLCRMessage = row.createCell(12);
                
                Cell averageHSRounds = row.createCell(13);
                Cell averageLCRRounds = row.createCell(14);
                
                processors.setCellValue(hs.Processors);
                
                minHSTime.setCellValue(hs.minTime);
                minLCRTime.setCellValue(lcr.minTime);
                
                maxHSTime.setCellValue(hs.maxTime);
                maxLCRTime.setCellValue(lcr.maxTime);
                
                averageHSTime.setCellValue(hs.averageTime);
                averageLCRTime.setCellValue(lcr.averageTime);
                
                minHSMessage.setCellValue(hs.minMessages);
                minLCRMessage.setCellValue(lcr.minMessages);
                
                maxHSMessage.setCellValue(hs.maxMessages);
                maxLCRMessage.setCellValue(lcr.maxMessages);
                
                averageHSMessage.setCellValue(hs.averageMessage);
                averageLCRMessage.setCellValue(lcr.averageMessage);
                
                averageHSRounds.setCellValue(hs.averageRounds);
                averageLCRRounds.setCellValue(lcr.averageRounds);
                
                
            }
            out =  new FileOutputStream(finalXlsxPath);
            workBook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(out != null){
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("successfully write to excel file!");
    }

    /**
     * @param in
     * @param filename
     * @return
     * @throws IOException
     * @throws InvalidFormatException 
     * @throws EncryptedDocumentException 
     */
    public static Workbook getWorkbok(File file) throws IOException, EncryptedDocumentException, InvalidFormatException{
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        if(file.getName().endsWith(EXCEL_XLS)){     //Excel&nbsp;2003
            wb = new HSSFWorkbook(in);
        }else if(file.getName().endsWith(EXCEL_XLSX)){    // Excel 2007/2010
           wb = WorkbookFactory.create(file);
        }
        return wb;
    }
}
