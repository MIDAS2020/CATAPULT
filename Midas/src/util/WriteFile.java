/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WriteFile {
	public WriteFile() {

	}

	public void writeToFile(ArrayList<Float> arr) {
		BufferedWriter bw = null;
		try {
			// Specify the file name and path here
			File file = new File("C:/Users/KaiHuangFudan/Davinci_debug.txt");

			// This logic will make sure that the file gets created if it is not present at
			// the specified location
			if (!file.exists())
				file.createNewFile();

			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			for (int i = 0; i < arr.size(); i++) {
				System.out.print(i);
				if (i % 20 == 0)
					System.out.println("<< nextLine");
				bw.write(arr.get(i).toString());
				bw.newLine();
			}
			System.out.println("File written Successfully");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (Exception ex) {
				System.out.println("Error in closing the BufferedWriter" + ex);
			}
		}
	}
}