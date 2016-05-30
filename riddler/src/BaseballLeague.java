
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * <pre>
 * FiveThirtyEight.com's <a href=
 * "http://fivethirtyeight.com/features/can-you-solve-the-puzzle-of-the-baseball-division-champs/">
 * Puzzle of the Can You Solve The Puzzle Of The Baseball Division Champs?</a>
 *
 * Assume you have a sport (let’s call it “baseball”) in which each team plays
 * 162 games in a season. Assume you have a division of five teams (call it the
 * “AL East”) where each team is of exact equal ability. Specifically, each team
 * has a 50 percent chance of winning each game. What is the expected value of
 * the number of wins for the team that finishes in first place?
 * 
 * Simulation to determine expected value.
 * </pre>
 * 
 * @author MPDroid
 *
 */
public class BaseballLeague {

  // Number of teams
  int M;

  // Number of games per team
  int N;

  /*
   * These arrays keep track of counts within a trial
   */

  // Total number of games played by teams i
  int[] games_I;

  // Total number of wins for team i
  int[] wins;

  /*
   * These variables keep track of the aggregates
   */
  // Running total winning score
  long totalWinningScore;

  // Expected winning score
  double expectedWinningScore;

  // Count of trials with one clear winner i.e. no draws
  long seriesWonCount;

  // Random outcome generator
  SecureRandom random;

  /**
   * @param numberOfTeams
   * @param numberOfGames
   */
  BaseballLeague(int numberOfTeams, int numberOfGames) {
    this.M = numberOfTeams;
    this.N = numberOfGames;
    random = new SecureRandom();
  }

  /**
   * Run the simulation
   * 
   * @param numberOfTrials
   */
  void simulate(int numberOfTrials) {

    long start = System.currentTimeMillis();
    for (int trial = 0; trial < numberOfTrials; trial++) {
      initilizeArrays();
      while (true) {
        for (int team_I = 0; team_I < M; team_I++) {
          for (int team_J = team_I + 1; team_J < M; team_J++) {
            if (hasTeamCompletedNTrials(team_J))
              continue;

            playMatch(team_I, team_J);

            if (hasTeamCompletedNTrials(team_I))
              break;
          }
        }
        if (haveAllTeamsCompletedNGames())
          break;
      }
      computeAggregates(trial);
    }

    printExpectedWinningScore(numberOfTrials);
    long elapsed = System.currentTimeMillis() - start;
    System.out.println("\nElapsed time " + elapsed + " ms");

  }

  private void initilizeArrays() {
    games_I = new int[M];
    wins = new int[M];
  }

  private boolean hasTeamCompletedNTrials(int team_I) {
    return games_I[team_I] == N;
  }

  private boolean haveAllTeamsCompletedNGames() {
    return hasTeamCompletedNTrials(4);
  }

  private int playMatch(int team_I, int team_J) {
    games_I[team_I]++;
    games_I[team_J]++;
    int outcome = random.nextInt(2);
    wins[team_I] += outcome;
    wins[team_J] += (1 - outcome);
    return outcome;
  }

  private void computeAggregates(int trial) {
    Arrays.sort(wins);
    if (hasClearWinner()) {

      seriesWonCount++;
      totalWinningScore += wins[M - 1];

      if (isLogPoint(trial))
        printExpectedWinningScore(trial + 1);
    }
  }

  private boolean isLogPoint(int trial) {
    return (trial + 1) % 10000 == 0;
  }

  private boolean hasClearWinner() {
    return wins[M - 1] > wins[M - 2];
  }

  private void printExpectedWinningScore(int trial) {
    expectedWinningScore = (double) totalWinningScore / seriesWonCount;
    System.out.format("\nExpected winning score %.2f based on %d trials", expectedWinningScore, (trial));
  }

  public static void main(String[] args) {
    BaseballLeague league = new BaseballLeague(5, 162);
    league.simulate(1000000);
  }
}
