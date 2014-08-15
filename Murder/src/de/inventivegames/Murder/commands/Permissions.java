package de.inventivegames.murder.commands;

import de.inventivegames.murder.Murder;

public enum Permissions {

	HELP {
		@Override
		public String perm() {
			return Murder.permBase + "player.help";
		}
	},
	JOIN {
		@Override
		public String perm() {
			return Murder.permBase + "player.join";
		}
	},
	LEAVE {
		@Override
		public String perm() {
			return Murder.permBase + "player.leave";
		}
	},
	ADDARENA {
		@Override
		public String perm() {
			return Murder.permBase + "admin.addarena";
		}
	},
	REMOVEARENA {
		@Override
		public String perm() {
			return Murder.permBase + "admin.removearena";
		}
	},
	ADDSPAWN {
		@Override
		public String perm() {
			return Murder.permBase + "admin.spawns";
		}
	},
	START {
		@Override
		public String perm() {
			return Murder.permBase + "admin.start";
		}
	},
	STOP {
		@Override
		public String perm() {
			return Murder.permBase + "admin.stop";
		}
	},
	SETNAME {
		@Override
		public String perm() {
			return Murder.permBase + "admin.setname";
		}
	},
	RELOAD {
		@Override
		public String perm() {
			return Murder.permBase + "admin.reload";
		}
	},
	SAVEARENA {
		@Override
		public String perm() {
			return Murder.permBase + "admin.savearena";
		}
	},
	CREATESIGN {
		@Override
		public String perm() {
			return Murder.permBase + "admin.createsign";
		}
	},
	ARENAINFO {
		@Override
		public String perm() {
			return Murder.permBase + "admin.arenainfo";
		}
	},
	PLAYERINFO {
		@Override
		public String perm() {
			return Murder.permBase + "admin.playerinfo";
		}
	},
	FORCE {
		@Override
		public String perm() {
			return Murder.permBase + "admin.force";
		}
	},
	TUTORIAL {
		@Override
		public String perm() {
			return Murder.permBase + "admin.tutorial";
		}
	},
	PLAYER {
		@Override
		public String perm() {
			return Murder.permBase + "player";
		}
	},
	ADMIN {
		@Override
		public String perm() {
			return Murder.permBase + "admin";
		}
	};

	public String perm() {
		return Murder.permBase;
	}

}
