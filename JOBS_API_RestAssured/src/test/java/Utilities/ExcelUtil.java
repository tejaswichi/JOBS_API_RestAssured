package Utilities;

import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelUtil {
	public static FileInputStream fi;
	public static FileOutputStream fo;

	public static int getRowCount(String xlFile, String xlSheet) throws IOException {
		Workbook workbook;
		Sheet sheet;
System.out.println("Inside getRowCount");
		fi = new FileInputStream(xlFile);
		workbook = WorkbookFactory.create(fi);
		sheet = workbook.getSheet(xlSheet);
		int rowCount = sheet.getLastRowNum();
	//	System.out.println("Inside getRowCount  :" +rowCount);
		workbook.close();
		fi.close();
		return rowCount;
	}

	public static int getCellCount(String xlFile, String xlSheet, int rowNum) throws IOException {
		
		Workbook workbook;
		Sheet sheet;
		Row row;
	//	System.out.println("Inside getCellCount");
		fi = new FileInputStream(xlFile);
		workbook = WorkbookFactory.create(fi);
		sheet = workbook.getSheet(xlSheet);
		row = sheet.getRow(rowNum);
		int columnCount = row.getLastCellNum();
	//	System.out.println("Inside getCellCount  :"+columnCount);
		workbook.close();
		fi.close();
		return columnCount;
	}

	public static String getCellData(String xlFile, String xlSheet, int rowNum, int columnNum) throws IOException {
		Workbook workbook;
		Sheet sheet;
		Row row;
		Cell cell;
		//System.out.println("Inside getCellData");
		fi = new FileInputStream(xlFile);
		workbook = WorkbookFactory.create(fi);
		sheet = workbook.getSheet(xlSheet);
		row = sheet.getRow(rowNum);
		cell = row.getCell(columnNum);
		String data;
		try {

			DataFormatter formatter = new DataFormatter();
			data = formatter.formatCellValue(cell);
			//System.out.println("Inside try method");

		} catch (Exception e) {
			//System.out.println("Inside catch method");
			data = "";
		}

		workbook.close();
		fi.close();
		return data;
	}

}
