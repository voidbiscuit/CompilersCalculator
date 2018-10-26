import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class code {

	public static void main(String[] args) {
		String calculation = readFileToArray("tim");
		calculator c = new calculator(calculation);
		//c.printTree();
		System.out.println(c.getCalculation() + " = " + c.answer());
	}

	private static String readFileToArray(String filename) {
		File f = new File(filename + ".calc");
		String data = "";
		String buffer = "";
		try {
			if (!f.exists())
				f.createNewFile();
			BufferedReader r = new BufferedReader(new FileReader(f));
			while (buffer != null) {
				data += buffer;
				buffer = r.readLine();
			}
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
}
