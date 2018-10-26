import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class calculator {
	private String calculation;
	private expression exp;
	private ArrayList<String> treemap = new ArrayList<String>();
	private ArrayList<String> language = new ArrayList<String>();
	private ArrayList<String> representation = new ArrayList<String>();
	private double answer;

	public calculator(String calculation) {
		readLanguageFiles();
		updateCalculation(calculation);
	}

	public void updateCalculation(String calculation) {
		calculation = calculation.replaceAll("[^\\d\\^\\/\\*\\+\\-\\)\\(\\.]+", "");
		for (int i = 0; i < calculation.length(); i++)
			if (calculation.charAt(i) == '-' && i > 0)
				if (("" + calculation.charAt(i - 1)).matches("[^\\d\\)]"))
					calculation = calculation.substring(0, i) + "n"
							+ calculation.substring(i + 1, calculation.length());
		calculation = fixBrackets(calculation);
		this.calculation = calculation;
		calculate();
	}

	public String getCalculation() {
		return calculation;
	}

	private String fixBrackets(String calculation) {
		int numberofopenbrackets = 0;
		int numberofclosedbrackets = 0;
		for (int i = 0; i < calculation.length(); i++) {
			if (calculation.charAt(i) == '(')
				numberofopenbrackets++;
			if (calculation.charAt(i) == ')')
				numberofclosedbrackets++;
		}
		int mag = (int) Math.abs(numberofopenbrackets - numberofclosedbrackets);
		if (mag > 0) {
			String brackets = "";
			for (int i = 0; i < mag; i++)
				brackets += (numberofopenbrackets < numberofclosedbrackets ? "(" : ")");
			if (numberofopenbrackets < numberofclosedbrackets)
				calculation = brackets + calculation;
			else
				calculation = calculation + brackets;
		}
		return calculation;
	}

	public double answer() {
		return answer;
	}

	private void readLanguageFiles() {
		File languagefile = new File("lang/language.lang");
		File representationfile = new File("lang/representation.lang");
		String buffer;
		BufferedReader r;
		BufferedReader s;
		try {
			if (!languagefile.exists())
				languagefile.createNewFile();
			if (!representationfile.exists())
				representationfile.createNewFile();
			buffer = "";
			r = new BufferedReader(new FileReader(languagefile));
			while (buffer != null) {
				if (buffer != "")
					language.add(buffer);
				buffer = r.readLine();
			}
			r.close();
			buffer = "";
			s = new BufferedReader(new FileReader(representationfile));
			while (buffer != null) {
				if (buffer != "")
					representation.add(buffer);
				buffer = s.readLine();
			}
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double calculate() {
		// System.out.println(language);
		// System.out.println(representation);
		int startbracket = 0;
		int endbracket = 0;
		// System.out.println(calculation);
		for (int i = 0; i < calculation.length(); i++) {
			if (i > 0)
				if (calculation.charAt(i) == '(' && calculation.charAt(i - 1) == ')')
					calculation = calculation.substring(0, i) + "*" + calculation.substring(i, calculation.length());
		}
		// System.out.println(calculation);
		Boolean bracketsdone = false;
		while (!bracketsdone) {
			// System.out.println(calculation);
			bracketsdone = true;
			for (int i = 0; i < calculation.length(); i++) {
				if (calculation.charAt(i) == '(') {
					bracketsdone = false;
					startbracket = i;
				}
				if (calculation.charAt(i) == ')') {
					bracketsdone = false;
					endbracket = i;
					break;
				}
			}
			if (!bracketsdone) {
				if (startbracket + 1 == endbracket)
					calculation = calculation.substring(0, startbracket) + 0
							+ calculation.substring(endbracket + 1, calculation.length());
				else {
					// System.out.println(" : " + startbracket + " " + endbracket);
					// System.out.println(calculation.substring(startbracket + 1, endbracket));
					expression exp = new expression(calculation.substring(startbracket + 1, endbracket));
					for (int i = 0; i < language.size() && i < representation.size(); i++)
						exp = splitTree(i, exp);
					double temp = navigate(exp);
					// System.out.println((temp < 0 ? "n" : "") + Math.abs(temp));
					calculation = calculation.substring(0, startbracket) + (temp < 0 ? "n" : "") + Math.abs(temp)
							+ calculation.substring(endbracket + 1, calculation.length());
				}
			}
			if (calculation != fixBrackets(calculation)) {
				calculation = fixBrackets(calculation);
				bracketsdone = false;
			}
		}
		exp = new expression(calculation);
		for (int i = 0; i < language.size() && i < representation.size(); i++)
			exp = splitTree(i, exp);
		answer = navigate(exp);
		return answer;
	}

	private expression splitTree(int i, expression exp) {
		String split = language.get(i);
		String represent = representation.get(i);
		return splitTree(exp, split, represent);
	}

	private expression splitTree(expression fragment, String split, String represent) {
		// Check if left branch exists first.
		if (fragment.leftExists())
			splitTree(fragment.getLeft(), split, represent);
		// If the branch needs to be split, things will be done
		String[] fragments = fragment.getData().split(split, 2);
		if (fragments[0] != fragment.getData()) {
			fragment.setData(represent);
			if (fragments.length > 0)
				fragment.setLeft(fragments[0]);
			if (fragments.length > 1)
				fragment.setRight(fragments[1]);
		}
		// Checks for a Right
		// Left only ever generates 1 branch, so we do not need to check
		// Left again.
		if (fragment.rightExists())
			splitTree(fragment.getRight(), split, represent);
		return fragment;
	}

	public void printTree() {
		treemap = new ArrayList<String>();
		printTree(exp, 0);
		for (String s : treemap)
			System.out.println(s);
	}

	private void printTree(expression node, int layer) {
		String spacer = "";
		int spacing = (int) Math.pow(2, 4 - layer);
		for (int i = 0; i < spacing; i++)
			spacer += " ";
		while (treemap.size() <= layer + 1)
			treemap.add(layer + " ");
		if (node.leftExists())
			printTree(node.getLeft(), layer + 1);
		else
			treemap = addToArrayList(treemap, layer + 1, spacer + "x" + spacer);
		treemap = addToArrayList(treemap, layer, spacer + spacer + node.getData() + spacer + spacer);
		if (node.rightExists())
			printTree(node.getRight(), layer + 1);
		else
			treemap = addToArrayList(treemap, layer + 1, spacer + "x" + spacer);
	}

	private ArrayList<String> addToArrayList(ArrayList<String> array, int index, String add) {
		array.set(index, array.get(index) + add);
		return array;

	}

	private double navigate(expression exp) {
		if (!exp.leftExists() || !exp.rightExists())
			return exp.getEquivalence();
		if (!exp.getLeft().hasEquivalence())
			navigate(exp.getLeft());
		if (!exp.getRight().hasEquivalence())
			navigate(exp.getRight());
		int token = -1;
		for (int i = 0; i < language.size(); i++)
			token = (exp.getData().matches(language.get(i))) ? i : token;
		exp.setEquivalence(operate(exp, token));
		// System.out.println(exp.getEquivalence());
		return exp.getEquivalence();
	}

	private double operate(expression exp, int token) {
		double left = (exp.getLeft().isNegative() ? -1 : 1) * exp.getLeft().getEquivalence();
		double right = (exp.getRight().isNegative() ? -1 : 1) * exp.getRight().getEquivalence();
		int pointer = 0;
		if (pointer++ == token)
			return left - right;
		if (pointer++ == token)
			return left + right;
		if (pointer++ == token)
			return left * right;
		if (pointer++ == token)
			return left / right;
		if (pointer++ == token)
			return Math.pow(left, right);

		return 0;
	}
}
