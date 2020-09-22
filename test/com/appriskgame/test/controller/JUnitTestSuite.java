package com.appriskgame.test.controller;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.appriskgame.strategy.Benevolent;
import com.appriskgame.test.strategy.AggressiveTest;
import com.appriskgame.test.strategy.BenevolentTest;
import com.appriskgame.test.strategy.CheaterTest;
import com.appriskgame.test.strategy.RandomPlayerTest;

@RunWith(Suite.class)
@SuiteClasses({ TournamentTest.class, MapOperationTest.class, MapValidationTest.class, PlayerTest.class,
		CardControllerTest.class, AggressiveTest.class, BenevolentTest.class, CheaterTest.class,
		RandomPlayerTest.class })

/**
 * A TestSuite class for testing all the test cases
 * 
 * @author Sahana
 *
 */
public class JUnitTestSuite {

}
