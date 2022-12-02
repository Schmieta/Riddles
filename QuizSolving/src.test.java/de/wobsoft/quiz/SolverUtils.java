package de.wobsoft.quiz;

import java.util.List;

import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;
import org.logicng.transformations.cnf.PlaistedGreenbaumTransformation;
import org.logicng.transformations.simplification.AdvancedSimplifier;
import org.logicng.transformations.simplification.AdvancedSimplifierConfig;




public class SolverUtils {
	SolverUtils() {
	}


	public static Formula simplify(Formula formula) {

		AdvancedSimplifierConfig config = AdvancedSimplifierConfig.builder()
				//
				.ratingFunction((x, cache) -> (int) x.numberOfOperands() + x.numberOfAtoms())//
				.factorOut(false)//
				.simplifyNegations(false)//
				.build();

		AdvancedSimplifier simplifier = new AdvancedSimplifier(config);
		Formula simplified = simplifier.apply(formula, true);
		return simplified;
	}

	public static Formula toCNF(Formula formula) {
		return formula.transform(new PlaistedGreenbaumTransformation());
	}

	public static List<Assignment> solve(Formula formula) {
		final SATSolver miniSat = MiniSat.glucose(formula.factory());
		for (Formula f : formula) {
			// System.out.println(f);
			miniSat.add(f);
		}

		final Tristate result = miniSat.sat();
		System.out.println(result);
		List<Assignment> models = miniSat.enumerateAllModels();
		int solutions = 0;
		for (Assignment assgn : models) {
			System.out.printf("%d  %s%n", solutions++, assgn.positiveVariables());
		}
		return models;

	}
	

}
