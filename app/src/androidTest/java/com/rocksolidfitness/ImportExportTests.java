package com.rocksolidfitness;

import android.content.Intent;
import android.os.Environment;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ImportExportTests extends ActivityUnitTestCase<BlankActivity>
{


    public ImportExportTests()
    {
        super(BlankActivity.class);
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


    public void testExportSess()
    {
        //ImpExpManager.exportAllSessions(context);


    }

    @SmallTest
    public void testImportExcel() throws Throwable
    {
        startActivity(new Intent(), null, null);
        runTestOnUiThread(new Runnable()
        {
            public void run()
            {
                ImpExpManager impExpManage = new ImpExpManager(Consts.EXPORT_CSV);
                impExpManage.execute(getActivity());
            }
        });
        assertNotNull(getActivity());

        // To wait for the AsyncTask to complete, you can safely call get() from the test thread
        //getActivity()._myAsyncTask.get();
        // assertTrue(asyncTaskRanCorrectly());
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
    }
}
