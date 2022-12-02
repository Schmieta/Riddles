package de.wobsoft.quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.logicng.datastructures.Assignment;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;

public class Quiz {

	FormulaFactory f;

	protected void printModel(Assignment assignment) {
		Map<Integer, List<String>> houses = new TreeMap<>();
		for (Variable x : assignment.positiveVariables()) {
			String v = x.name();
			int dot = v.indexOf('.');
			int house = Integer.valueOf(v.substring(dot + 1, dot + 2));
			houses.putIfAbsent(house, new ArrayList<>());
			houses.get(house).add(v.substring(0, dot));
		}

		for (Map.Entry<Integer, List<String>> x : houses.entrySet()) {
			System.out.printf("%d\t%s%n", x.getKey(), x.getValue());
		}
	}

	protected Formula equivalence(Enum<?> x, Enum<?> y, int n) {
		return equivalence(x.name(), y.name(), n);
	}

	private Formula equivalence(String x, String y, int n) {
		List<Formula> r = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			r.add(f.equivalence(pair(x, i + 1), pair(y, i + 1)));
		}
		return f.and(r);
	}

	Formula nand(Formula x, Formula y) {
		return f.not(f.and(x, y));
	}

	Formula nand(Enum<?> x, Enum<?> y, int n) {
		List<Formula> r = new ArrayList<>();
		for (int i = 1; i <= n; i++) {
			r.add(f.not(f.and(pair(x, i), pair(y, i))));
		}
		return f.and(r);
	}

	protected Formula nextTo(Enum<?> x, Enum<?> y, int n) {
		return f.or(leftTo(x, y, n), rightTo(x, y, n));
	}

	protected Formula rightTo(Enum<?> x, Enum<?> y, int n) {
		List<Formula> r = new ArrayList<>();
		//
		for (int i = n; i > 1; i--) {
			r.add(f.equivalence(pair(x, i), pair(y, i - 1)));
		}

		r.add(f.equivalence(pair(x, 2), pair(y, 1)));
		// exclude these
		r.add(f.not(pair(x, 1)));
		r.add(f.not(pair(y, n)));
		return f.and(r);
	}

	private Formula leftTo(Enum<?> x, Enum<?> y, int n) {
		List<Formula> r = new ArrayList<>();
		//
		for (int i = 1; i < n; i++) {
			r.add(f.equivalence(pair(x, i), pair(y, i + 1)));
		}
		// exclude these
		r.add(f.not(pair(x, n)));
		r.add(f.not(pair(y, 1)));
		return f.and(r);
	}

	Formula gt(Enum<?> x, Enum<?> y, int n) {
		List<Formula> r = new ArrayList<>();
		for (int i = 1; i <= n; i++) {
			for (int j = i; j <= n; j++) {
				r.add(nand(pair(x, i), pair(y, j)));
			}
		}

		return f.and(r);
	}

	/**
	 * Encodes a cartesian product of var[n] x n. Each row and each column of the
	 * nxn-matrix forms an exo() aka exactly one - condition. Alle exo's will be
	 * linked by the AND-Operator.
	 * 
	 * @param var an array of variables
	 * @param n   the number of varuables indexes
	 * @return the permuation encoding
	 */
	protected Formula permutation(String[] var, int n) {
		Variable[][] matrix = new Variable[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				matrix[i][j] = pair(var[j], i + 1);
			}
		}
		List<Formula> collect = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			Variable[] col = new Variable[n];
			collect.add(f.exo(matrix[i]));
			for (int j = 0; j < n; j++) {
				col[j] = matrix[j][i];
			}
			collect.add(f.exo(col));
		}
		return f.and(collect);
	}

	/**
	 * Creates a variable of pattern <name>.<index>
	 * 
	 * @param name
	 * @param index
	 * @return the variable
	 */
	private Variable pair(String name, int index) {
		return f.variable(name + "." + index);
	}

	protected Variable pair(Enum<?> en, int index) {
		return pair(en.name(), index);
	}

	/**
	 * num class lietral to string array.
	 * 
	 * @param <T>
	 * @param enm
	 * @return String[]
	 */
	protected <T extends Enum<T>> String[] toArray(Class<T> enm) {
		List<String> enumNames = Stream.of(enm.getEnumConstants()).map(Enum::name).collect(Collectors.toList());
		return enumNames.toArray(new String[enumNames.size()]);
	}

}
