package Helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;


public class ReadXLSheet {
	FileInputStream fs = null;
	WorkbookSettings ws = null;
	Workbook workbook = null;
	Sheet s = null;
	
	public ReadXLSheet(String filePath, String sheetName) {
		try {
			fs = new FileInputStream(new File(filePath));
			ws = new WorkbookSettings();
			ws.setLocale(new Locale("en", "EN"));
			workbook = Workbook.getWorkbook(fs, ws);
			s = workbook.getSheet(sheetName);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeXLFile() {
		try {
			workbook.close();	
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getRowsNum() {
		return s.getRows();
	}
	
	
	//Returns the Headings used inside the excel sheet
	public void getHeadingFromXlsFile() {
		int columnCount = s.getColumns();
		for (int i = 0; i < columnCount; i++) {
			System.out.print(s.getCell(i, 0).getContents()+" ");
		}
		System.out.println();
	}
	
	public String[] rowReading(int rowNum) {
		Cell rowData[] = null;
		int columnCount = s.getColumns();
		
		String[] outputData= new String[columnCount];
		
		rowData = s.getRow(rowNum);

		if (rowData[0].getContents().length() != 0) { // the first date column must not null
			for (int j = 0; j < columnCount; j++) {
				outputData[j]=rowData[j].getContents();

				}
			}
		return outputData;
	}
					

}