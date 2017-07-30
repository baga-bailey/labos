package uk.co.mafew.file.office;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CsvToXslx
{

	public static void main(String[] args)
	{
		CsvToXslx ctx = new CsvToXslx();
		ctx.csvToXLSX("C:\\Users\\jbailey1\\Desktop\\test.csv", "C:\\Users\\jbailey1\\Desktop\\test.xslx");
	}

	public void csvToXLSX(String csvFile, String xlsxFile)
	{
		try
		{
			XSSFWorkbook workBook = new XSSFWorkbook();
			XSSFSheet sheet = workBook.createSheet("sheet1");
			String currentLine = null;
			int RowNum = 0;
			BufferedReader br = new BufferedReader(new FileReader(csvFile));
			while ((currentLine = br.readLine()) != null)
			{
				String str[] = currentLine.split(",");
				RowNum++;
				XSSFRow currentRow = sheet.createRow(RowNum);
				for (int i = 0; i < str.length; i++)
				{
					currentRow.createCell(i).setCellValue(str[i]);
				}
			}

			FileOutputStream fileOutputStream = new FileOutputStream(xlsxFile);
			workBook.write(fileOutputStream);
			fileOutputStream.close();
			System.out.println("Done");
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage() + "Exception in try");
		}
	}

}
