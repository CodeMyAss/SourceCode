package de.inventivegames.murder;

public enum ArenaStatus {

	WAITING {
		@Override
		public String toString() {
			return "§2[Waiting]";
		}
	},
	FULL {
		@Override
		public String toString() {
			return "§c[Full]";
		}
	},
	INGAME {
		@Override
		public String toString() {
			return "§c[Ingame]";
		}
	},
	END, OFFLINE {
		@Override
		public String toString() {
			return "§7>Offline<";
		}
	};

}
