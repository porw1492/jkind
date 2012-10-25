package jkind.solvers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jkind.solvers.SolverResult.Result;

public class YicesSolver {
	private Process process;
	private BufferedWriter toYices;
	private BufferedReader fromYices;

	final private static String DONE = "__DONE__";

	public YicesSolver() throws IOException {
		ProcessBuilder pb = new ProcessBuilder("yices");
		pb.redirectErrorStream(true);
		process = pb.start();
		toYices = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		fromYices = new BufferedReader(new InputStreamReader(process.getInputStream()));
	}

	public void stop() {
		try {
			toYices.close();
			fromYices.close();
		} catch (IOException e) {
		}
		process.destroy();
		process = null;
	}

	public void send(String str) throws IOException {
		toYices.append(str);
		toYices.newLine();
		toYices.flush();
	}

	final private static Pattern valuePattern = Pattern.compile("\\(= (\\S+) (\\S+) \\)");
	final private static Pattern functionPattern = Pattern
			.compile("\\(= \\((\\S+) (\\S+)\\) (\\S+)\\)");

	public SolverResult query(String str) throws IOException {
		send("(push)");
		send("(assert " + str + ")");
		send("(echo \"" + DONE + "\")");
		send("(pop)");

		Result result = null;
		Model model = new Model();
		while (true) {
			String line = fromYices.readLine();
			if (line.equals(DONE)) {
				break;
			} else if (line.equals("unsat")) {
				result = Result.UNSAT;
			} else if (line.equals("sat")) {
				result = Result.SAT;
			} else {
				Matcher m = valuePattern.matcher(line);
				if (m.matches()) {
					model.addValue(m.group(1), parseValue(m.group(2)));
				} else {
					m = functionPattern.matcher(line);
					if (m.matches()) {
						model.addFunctionValue(m.group(1), Integer.parseInt(m.group(2)),
								parseValue(m.group(3)));
					}
				}
			}
		}
		return new SolverResult(result, model);
	}

	private Value parseValue(String str) {
		if (str.equals("true")) {
			return BoolValue.TRUE;
		} else if (str.equals("false")) {
			return BoolValue.FALSE;
		} else {
			return new NumericValue(str);
		}
	}
}
