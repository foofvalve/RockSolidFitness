package com.rocksolidfitness;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ImportExportTests extends AndroidTestCase
{
    private RenamingDelegatingContext context;

    public void setUp()
    {
        context = new RenamingDelegatingContext(getContext(), "test_v3_");
    }

    public void testBlah()
            throws BiffException, IOException, WriteException
    {
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setUseTemporaryFileDuringWrite(true);

        //get the sdcard's directory
        File sdCard = Environment.getExternalStorageDirectory();
        //add on the your app's path
        File dir = new File(sdCard.getAbsolutePath());
        //make them in case they're not there
        dir.mkdirs();
        //create a standard java.io.File object for the Workbook to use
        File wbfile = new File(dir, "testingExport.xls");


        WritableWorkbook wworkbookwb = null;

        wworkbookwb = Workbook.createWorkbook(wbfile, wbSettings);
        WritableSheet wsheet = wworkbookwb.createSheet("First Sheet", 0);
        Label label = new Label(0, 2, "A label record");
        wsheet.addCell(label);

        wworkbookwb.write();
        wworkbookwb.close();

    }

    public void testImportExcel()
    {

    }

    public void tearDown() throws Exception
    {
        super.tearDown();
    }
}
