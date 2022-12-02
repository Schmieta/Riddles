package de.wobsoft.quiz;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logicng.datastructures.Assignment;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

/**
 * 22.11.22
 * Solves the Zebra riddle with LogicNG. <br>
 * Source: https://code.energy/solving-zebra-puzzle/ <br>
 * <p>
 * The quiz:
 * 
 * <pre>
   1 There are five houses.
   2 The Englishman lives in the red house.
   3 The Spaniard owns the dog.
   4 Coffee is drunk in the green house.
   5 The Ukrainian drinks tea.
   6 The green house is immediately to the right of the ivory house.
   7 The Old Gold smoker owns snails.
   8 Kools are smoked in the yellow house.
   9 Milk is drunk in the middle house.
   10 The Norwegian lives in the first house.
   11 The man who smokes Chesterfields lives in the house next to the man with the fox.
   12 Kools are smoked in the house next to the house where the horse is kept.
   12 The Lucky Strike smoker drinks orange juice.
   13 The Japanese smokes Parliaments.
   14 The Norwegian lives next to the blue house.
 * </pre>
 * 
 * @author CS
 *
 *
 */
class ZebraQuiz extends Quiz {

	private static final int n = 5;



	enum Nationality {
		English, Spanish, Ukrainian, Norwegian, Japanese
	};

	enum Pet {
		Dog, Snails, Fox, Horse, Zebra
	};

	enum Cigarette {
		Old_Gold, Kools, Chesterfields, Lucky_Strike, Parliaments
	};

	enum Colour {
		Red, Green, Yellow, Blue, Ivory
	};

	enum Beverage {
		Coffee, Milk, Orange_juice, Water, Tea
	};

	@BeforeEach
	public void setUp() {
		f = new FormulaFactory();
	}

	@AfterEach
	public void tearDown() {

	}

	/**
	 * Listening to yellowjackets Intrigue An encoding of the zebra riddle.
	 * @throws IOException 
	 */
	@Test
	void solve() throws IOException {

		Formula nationality = permutation(toArray(Nationality.class), n);
		
	//	BDDDotFileWriter.write("nationality", nationality.cnf().bdd());
		Formula colour = permutation(toArray(Colour.class), n);
		Formula beverage = permutation(toArray(Beverage.class), n);
		Formula cigarette = permutation(toArray(Cigarette.class), n);
		Formula pet = permutation(toArray(Pet.class), n);

		// 2 The Englishman lives in the red house.
		Formula p2 = equivalence(Nationality.English, Colour.Red, n);

		// 3 The Spaniard owns the dog.
		Formula p3 = equivalence(Nationality.Spanish, Pet.Dog, n);

		// 4 Coffee is drunk in the green house.
		Formula p4 = equivalence(Beverage.Coffee, Colour.Green, n);

		// 5 The Ukrainian drinks tea.
		Formula p5 = equivalence(Nationality.Ukrainian, Beverage.Tea, n);

		// 6 The green house is immediately to the right of the ivory house.
		Formula p6 = rightTo(Colour.Green, Colour.Ivory, n);

		// 7 The Old Gold smoker owns snails.
		Formula p7 = equivalence(Cigarette.Old_Gold, Pet.Snails, n);

		// 8 Kools are smoked in the yellow house.
		Formula p8 = equivalence(Cigarette.Kools, Colour.Yellow, n);

		// 9 Milk is drunk in the middle house.
		Formula p9 = f.equivalence(pair(Beverage.Milk, 3), f.verum());

		// 10 The Norwegian lives in the first house.
		Formula p10 = f.equivalence(pair(Nationality.Norwegian, 1), f.verum());

		// 11 The man who smokes Chesterfields lives in the house next to the man with
		// the fox.
		Formula p11 = nextTo(Cigarette.Chesterfields, Pet.Fox, n);

		// 12 The Lucky Strike smoker drinks orange juice.
		Formula p12 = equivalence(Cigarette.Lucky_Strike, Beverage.Orange_juice, n);

		// 13 The Japanese smokes Parliaments.
		Formula p13 = equivalence(Nationality.Japanese, Cigarette.Parliaments, n);

		// 14 The Norwegian lives next to the blue house.
		Formula p14 = nextTo(Nationality.Norwegian, Colour.Blue, n);

		// 15 Kools are smoked in the house next to the house where the horse is kept.
		Formula p15 = nextTo(Cigarette.Kools, Pet.Horse, n);
		//
		Formula formula = f.and(nationality, colour, pet, beverage, cigarette, p2, p3, p4, p5, p7, p8, p9, p10, p12,
				p13, p14, p6, p15, p11);
		// System.out.printf("bdd-nodes %s%n", formula.bdd().nodeCount());

		// solve
		List<Assignment> models = SolverUtils.solve(formula);

		assertThat(models).isNotNull().hasSize(1);
		printModel(models.get(0));
	}
	

	
}
