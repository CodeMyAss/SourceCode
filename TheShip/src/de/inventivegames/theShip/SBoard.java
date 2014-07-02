package de.inventivegames.theShip;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class SBoard {

	private Player							p;
	private ScoreboardManager				manager;
	private Scoreboard						board;
	private Objective						obj;

	private Score							quarryTitle;
	private Score							quarry;

	private Score							space1;

	private Score							status1;
	private Score							status2;
	private Score							status3;

	private Score							space2;

	private Score							timeTitle;
	private Score							time;

	private Score							space3;

	private Score							moneyTitle;
	private Score							money;

	private int								SBTimer			= -1;

	private int								quarryTitlePos	= 12;
	private int								quarryPos		= 11;
	private int								space1Pos		= 10;
	private int								timeTitlePos	= 9;
	private int								timePos			= 8;
	private int								space2Pos		= 7;
	private int								moneyTitlePos	= 6;
	private int								moneyPos		= 5;
	private int								space3Pos		= 4;
	private int								indicator1Pos	= 3;
	private int								indicator2Pos	= 2;
	private int								indicator3Pos	= 1;

	private static HashMap<Player, SBoard>	boards			= new HashMap<Player, SBoard>();

	@SuppressWarnings("deprecation")
	public SBoard(Player p) {
		this.p = p;
		this.manager = TheShip.instance.getServer().getScoreboardManager();
		this.board = manager.getNewScoreboard();
		this.obj = board.registerNewObjective("TheShipSBoard", "dummy");
		this.obj.setDisplayName(TheShip.SBTitle);
		this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		this.space1 = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer("§0 "));
		this.space2 = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer("§1 "));
		this.space3 = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer("§2 "));

		SBoard.boards.put(p, this);
	}

	public static SBoard getBoard(Player p) {
		if (p != null) {
			if (!(boards.containsKey(p))) {
				return new SBoard(p);
			} else {
				return boards.get(p);
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public void setQuarry(ShipPlayer sp, String oldQuarry) {
		String name = "Waiting...";
		if (sp != null) {
			name = sp.getPlayer().getName();
		} else {
			this.board.resetScores(TheShip.instance.getServer().getOfflinePlayer(oldQuarry));
		}

		this.quarryTitle = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer("§2Quarry:"));
		this.quarryTitle.setScore(this.quarryTitlePos);
		this.quarry = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer(name));
		this.quarry.setScore(this.quarryPos);
	}

	@SuppressWarnings("deprecation")
	public void setStatus(boolean status) {
		status = !status;
		String indicator = "\u2588";

		if (status) {
			board.resetScores(TheShip.instance.getServer().getOfflinePlayer("§0§c" + indicator + indicator + indicator));
			board.resetScores(TheShip.instance.getServer().getOfflinePlayer("§1§c" + indicator + indicator + indicator));
			board.resetScores(TheShip.instance.getServer().getOfflinePlayer("§2§c" + indicator + indicator + indicator));

			this.status1 = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer("§0§2" + indicator + indicator + indicator));
			this.status1.setScore(this.indicator1Pos);
			this.status2 = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer("§1§2" + indicator + indicator + indicator));
			this.status2.setScore(this.indicator2Pos);
			this.status3 = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer("§2§2" + indicator + indicator + indicator));
			this.status3.setScore(this.indicator3Pos);

			this.space1.setScore(this.space1Pos);
		}

		if (!status) {
			board.resetScores(TheShip.instance.getServer().getOfflinePlayer("§0§2" + indicator + indicator + indicator));
			board.resetScores(TheShip.instance.getServer().getOfflinePlayer("§1§2" + indicator + indicator + indicator));
			board.resetScores(TheShip.instance.getServer().getOfflinePlayer("§2§2" + indicator + indicator + indicator));

			this.status1 = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer("§0§c" + indicator + indicator + indicator));
			this.status1.setScore(this.indicator1Pos);
			this.status2 = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer("§1§c" + indicator + indicator + indicator));
			this.status2.setScore(this.indicator2Pos);
			this.status3 = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer("§2§c" + indicator + indicator + indicator));
			this.status3.setScore(this.indicator3Pos);

			this.space1.setScore(this.space1Pos);
		}
	}

	@SuppressWarnings("deprecation")
	public void resetOldMoney(int i) {
		board.resetScores(TheShip.instance.getServer().getOfflinePlayer("" + i + "$"));
	}
	
	@SuppressWarnings("deprecation")
	public void updateMoney(int i) {
		this.moneyTitle = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer("§2Money"));
		this.moneyTitle.setScore(this.moneyTitlePos);

		this.money = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer("" + i + "$"));
		this.money.setScore(this.moneyPos);
		
		this.space3.setScore(this.space3Pos);
	}

	@SuppressWarnings("deprecation")
	public void startTimer(int t) {

		final int i = t / 20;

		this.space2.setScore(this.space2Pos);

		this.timeTitle = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer("§2Time Left"));
		this.timeTitle.setScore(this.timeTitlePos);

		String[] cTime = convertTime(i);
		String min = cTime[0];
		String sec = cTime[1];

		String fullTime = min + ":" + sec;

		setTime(fullTime);

		SBTimer = TheShip.instance.getServer().getScheduler().scheduleSyncRepeatingTask(TheShip.instance, new Runnable() {

			String	currentString	= null;

			int		time			= i;

			@Override
			public void run() {
				if (currentString != null) {
					board.resetScores(TheShip.instance.getServer().getOfflinePlayer(currentString));
				}

				String[] cTime = convertTime(time);
				String min = cTime[0];
				String sec = cTime[1];

				String fullTime = min + ":" + sec;

				setTime(fullTime);

				currentString = fullTime;

				time--;

				if (time < 0) {
					TheShip.instance.getServer().getScheduler().cancelTask(SBTimer);
				}
			}
		}, 20, 20);
	}

	@SuppressWarnings("deprecation")
	private void setTime(String time) {
		this.time = this.obj.getScore(TheShip.instance.getServer().getOfflinePlayer(time));
		this.time.setScore(this.timePos);
	}

	private String[] convertTime(int seconds) {

		// int hours = seconds / 3600;
		int minutes = (seconds % 3600) / 60;
		seconds = seconds % 60;

		return new String[] { twoDigitString(minutes), twoDigitString(seconds) };
	}

	private String twoDigitString(int number) {

		if (number == 0) {
			return "00";
		}

		if (number / 10 == 0) {
			return "0" + number;
		}

		return String.valueOf(number);
	}

	public void updateDisplay() {
		this.p.setScoreboard(this.board);
	}

	public void resetBoard() {
		this.p.setScoreboard(this.manager.getNewScoreboard());
	}

}