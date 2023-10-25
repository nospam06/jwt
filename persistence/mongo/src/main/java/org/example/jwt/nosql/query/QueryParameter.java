package org.example.jwt.nosql.query;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryParameter {
	public static final boolean TO_NUMBER = true;
	public static final boolean NO_CONVERSION = false;
	private final List<WhereClause> whereClauses = new ArrayList<>();
	private final List<OrderBy> orderBy = new ArrayList<>();
	private final List<String> columns = new ArrayList<>();
	private String groupBy = null;
	private String having = null;
	private Integer limit;

	public static Builder builder() {
		return new Builder().or();
	}

	public static Builder emptyBuilder() {
		return new Builder();
	}

	public static class Builder {
		private final QueryParameter parameter = new QueryParameter();

		public Builder or() {
			parameter.whereClauses.add(new WhereClause());
			return this;
		}

		public Builder whereEqual(String column, Object value) {
			getLastWhereClause().add(new WhereEqual(column, value, false));
			return this;
		}

		public Builder whereNotEqual(String column, Object value) {
			getLastWhereClause().add(new WhereEqual(column, value, true));
			return this;
		}

		public Builder whereLike(String column, String value) {
			getLastWhereClause().add(new WhereLike(column, value, false));
			return this;
		}

		public Builder whereNotLike(String column, String value) {
			getLastWhereClause().add(new WhereLike(column, value, true));
			return this;
		}

		public Builder whereBetween(String column, Object from, Object to, boolean toNumber) {
			getLastWhereClause().add(new WhereBetween(column, from, to, toNumber, false));
			return this;
		}

		public Builder whereNotBetween(String column, Object from, Object to, boolean toNumber) {
			getLastWhereClause().add(new WhereBetween(column, from, to, toNumber, true));
			return this;
		}

		public Builder whereIn(String column, List<? extends Serializable> value) {
			getLastWhereClause().add(new WhereIn(column, value, false));
			return this;
		}

		public Builder whereNotIn(String column, List<? extends Serializable> value) {
			getLastWhereClause().add(new WhereIn(column, value, true));
			return this;
		}

		public Builder whereMissing(String column) {
			getLastWhereClause().add(new WhereMissing(column, false));
			return this;
		}

		public Builder whereNotMissing(String column) {
			getLastWhereClause().add(new WhereMissing(column, true));
			return this;
		}

		public Builder columns(String column) {
			parameter.columns.add(column);
			return this;
		}

		public Builder groupBy(String groupBy) {
			parameter.groupBy = groupBy;
			return this;
		}

		public Builder orderBy(String column, boolean ascending, boolean convertToNumber) {
			parameter.orderBy.add(OrderBy.builder().column(column).ascending(ascending).convertToNumber(convertToNumber).build());
			return this;
		}

		public Builder having(String having) {
			parameter.having = having;
			return this;
		}

		public Builder limit(int limit) {
			parameter.limit = limit;
			return this;
		}

		public QueryParameter build() {
			return parameter;
		}

		private WhereClause getLastWhereClause() {
			return parameter.whereClauses.get(parameter.whereClauses.size() - 1);
		}
	}

	@Getter
	public static class WhereClause {
		private final List<WhereEqual> equals = new ArrayList<>();
		private final List<WhereLike> likes = new ArrayList<>();
		private final List<WhereBetween> betweens = new ArrayList<>();
		private final List<WhereIn> ins = new ArrayList<>();
		private final List<WhereMissing> missings = new ArrayList<>();

		void add(WhereEqual equal) {
			equals.add(equal);
		}

		void add(WhereLike like) {
			likes.add(like);
		}

		void add(WhereBetween between) {
			betweens.add(between);
		}

		void add(WhereIn in) {
			ins.add(in);
		}

		void add(WhereMissing missing) {
			missings.add(missing);
		}
	}

	@Getter
	@AllArgsConstructor
	public static class WhereEqual {
		private final String column;
		private final Object value;
		private final boolean not;
	}

	@Getter
	@AllArgsConstructor
	public static class WhereLike {
		private final String column;
		private final String value;
		private final boolean not;
	}

	@Getter
	@AllArgsConstructor
	public static class WhereBetween {
		private final String column;
		private final Object from;
		private final Object to;
		private final boolean toNumber;
		private final boolean not;
	}

	@Getter
	@AllArgsConstructor
	public static class WhereIn {
		private final String column;
		private final List<? extends Serializable> in;
		private final boolean not;
	}

	@Getter
	@AllArgsConstructor
	public static class WhereMissing {
		private final String column;
		private final boolean not;
	}

	@lombok.Builder
	@Getter
	public static class OrderBy {
		private final String column;
		private final boolean ascending;
		private final boolean convertToNumber;
	}
}
