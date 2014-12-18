package ru.taxims.domain.datamodels;

/**
 * Created by Developer_DB on 27.11.14.
 */
public enum OrderDistributionStates
{
		OFFER(1),
		ASSIGN(2),
		REFUSE(3);

		private final int state;

		OrderDistributionStates(int state) {
			this.state = state;
		}
		public int state() { return state; }

}
