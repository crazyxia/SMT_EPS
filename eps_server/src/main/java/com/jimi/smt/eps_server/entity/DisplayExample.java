package com.jimi.smt.eps_server.entity;

import java.util.ArrayList;
import java.util.List;

public class DisplayExample {
    protected String orderByClause;
	protected boolean distinct;
	protected List<Criteria> oredCriteria;

	public DisplayExample() {
		oredCriteria = new ArrayList<Criteria>();
	}

	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	public String getOrderByClause() {
		return orderByClause;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}

	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}

	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	public Criteria createCriteria() {
		Criteria criteria = createCriteriaInternal();
		if (oredCriteria.size() == 0) {
			oredCriteria.add(criteria);
		}
		return criteria;
	}

	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}

	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	protected abstract static class GeneratedCriteria {
		protected List<Criterion> criteria;

		protected GeneratedCriteria() {
			super();
			criteria = new ArrayList<Criterion>();
		}

		public boolean isValid() {
			return criteria.size() > 0;
		}

		public List<Criterion> getAllCriteria() {
			return criteria;
		}

		public List<Criterion> getCriteria() {
			return criteria;
		}

		protected void addCriterion(String condition) {
			if (condition == null) {
				throw new RuntimeException("Value for condition cannot be null");
			}
			criteria.add(new Criterion(condition));
		}

		protected void addCriterion(String condition, Object value, String property) {
			if (value == null) {
				throw new RuntimeException("Value for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value));
		}

		protected void addCriterion(String condition, Object value1, Object value2, String property) {
			if (value1 == null || value2 == null) {
				throw new RuntimeException("Between values for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value1, value2));
		}

		public Criteria andLineIsNull() {
			addCriterion("line is null");
			return (Criteria) this;
		}

		public Criteria andLineIsNotNull() {
			addCriterion("line is not null");
			return (Criteria) this;
		}

		public Criteria andLineEqualTo(Integer value) {
			addCriterion("line =", value, "line");
			return (Criteria) this;
		}

		public Criteria andLineNotEqualTo(Integer value) {
			addCriterion("line <>", value, "line");
			return (Criteria) this;
		}

		public Criteria andLineGreaterThan(Integer value) {
			addCriterion("line >", value, "line");
			return (Criteria) this;
		}

		public Criteria andLineGreaterThanOrEqualTo(Integer value) {
			addCriterion("line >=", value, "line");
			return (Criteria) this;
		}

		public Criteria andLineLessThan(Integer value) {
			addCriterion("line <", value, "line");
			return (Criteria) this;
		}

		public Criteria andLineLessThanOrEqualTo(Integer value) {
			addCriterion("line <=", value, "line");
			return (Criteria) this;
		}

		public Criteria andLineIn(List<Integer> values) {
			addCriterion("line in", values, "line");
			return (Criteria) this;
		}

		public Criteria andLineNotIn(List<Integer> values) {
			addCriterion("line not in", values, "line");
			return (Criteria) this;
		}

		public Criteria andLineBetween(Integer value1, Integer value2) {
			addCriterion("line between", value1, value2, "line");
			return (Criteria) this;
		}

		public Criteria andLineNotBetween(Integer value1, Integer value2) {
			addCriterion("line not between", value1, value2, "line");
			return (Criteria) this;
		}

		public Criteria andWorkOrderIsNull() {
			addCriterion("work_order is null");
			return (Criteria) this;
		}

		public Criteria andWorkOrderIsNotNull() {
			addCriterion("work_order is not null");
			return (Criteria) this;
		}

		public Criteria andWorkOrderEqualTo(String value) {
			addCriterion("work_order =", value, "workOrder");
			return (Criteria) this;
		}

		public Criteria andWorkOrderNotEqualTo(String value) {
			addCriterion("work_order <>", value, "workOrder");
			return (Criteria) this;
		}

		public Criteria andWorkOrderGreaterThan(String value) {
			addCriterion("work_order >", value, "workOrder");
			return (Criteria) this;
		}

		public Criteria andWorkOrderGreaterThanOrEqualTo(String value) {
			addCriterion("work_order >=", value, "workOrder");
			return (Criteria) this;
		}

		public Criteria andWorkOrderLessThan(String value) {
			addCriterion("work_order <", value, "workOrder");
			return (Criteria) this;
		}

		public Criteria andWorkOrderLessThanOrEqualTo(String value) {
			addCriterion("work_order <=", value, "workOrder");
			return (Criteria) this;
		}

		public Criteria andWorkOrderLike(String value) {
			addCriterion("work_order like", value, "workOrder");
			return (Criteria) this;
		}

		public Criteria andWorkOrderNotLike(String value) {
			addCriterion("work_order not like", value, "workOrder");
			return (Criteria) this;
		}

		public Criteria andWorkOrderIn(List<String> values) {
			addCriterion("work_order in", values, "workOrder");
			return (Criteria) this;
		}

		public Criteria andWorkOrderNotIn(List<String> values) {
			addCriterion("work_order not in", values, "workOrder");
			return (Criteria) this;
		}

		public Criteria andWorkOrderBetween(String value1, String value2) {
			addCriterion("work_order between", value1, value2, "workOrder");
			return (Criteria) this;
		}

		public Criteria andWorkOrderNotBetween(String value1, String value2) {
			addCriterion("work_order not between", value1, value2, "workOrder");
			return (Criteria) this;
		}

		public Criteria andBoardTypeIsNull() {
			addCriterion("board_type is null");
			return (Criteria) this;
		}

		public Criteria andBoardTypeIsNotNull() {
			addCriterion("board_type is not null");
			return (Criteria) this;
		}

		public Criteria andBoardTypeEqualTo(Integer value) {
			addCriterion("board_type =", value, "boardType");
			return (Criteria) this;
		}

		public Criteria andBoardTypeNotEqualTo(Integer value) {
			addCriterion("board_type <>", value, "boardType");
			return (Criteria) this;
		}

		public Criteria andBoardTypeGreaterThan(Integer value) {
			addCriterion("board_type >", value, "boardType");
			return (Criteria) this;
		}

		public Criteria andBoardTypeGreaterThanOrEqualTo(Integer value) {
			addCriterion("board_type >=", value, "boardType");
			return (Criteria) this;
		}

		public Criteria andBoardTypeLessThan(Integer value) {
			addCriterion("board_type <", value, "boardType");
			return (Criteria) this;
		}

		public Criteria andBoardTypeLessThanOrEqualTo(Integer value) {
			addCriterion("board_type <=", value, "boardType");
			return (Criteria) this;
		}

		public Criteria andBoardTypeIn(List<Integer> values) {
			addCriterion("board_type in", values, "boardType");
			return (Criteria) this;
		}

		public Criteria andBoardTypeNotIn(List<Integer> values) {
			addCriterion("board_type not in", values, "boardType");
			return (Criteria) this;
		}

		public Criteria andBoardTypeBetween(Integer value1, Integer value2) {
			addCriterion("board_type between", value1, value2, "boardType");
			return (Criteria) this;
		}

		public Criteria andBoardTypeNotBetween(Integer value1, Integer value2) {
			addCriterion("board_type not between", value1, value2, "boardType");
			return (Criteria) this;
		}

		public Criteria andSelectedIsNull() {
			addCriterion("selected is null");
			return (Criteria) this;
		}

		public Criteria andSelectedIsNotNull() {
			addCriterion("selected is not null");
			return (Criteria) this;
		}

		public Criteria andSelectedEqualTo(Boolean value) {
			addCriterion("selected =", value, "selected");
			return (Criteria) this;
		}

		public Criteria andSelectedNotEqualTo(Boolean value) {
			addCriterion("selected <>", value, "selected");
			return (Criteria) this;
		}

		public Criteria andSelectedGreaterThan(Boolean value) {
			addCriterion("selected >", value, "selected");
			return (Criteria) this;
		}

		public Criteria andSelectedGreaterThanOrEqualTo(Boolean value) {
			addCriterion("selected >=", value, "selected");
			return (Criteria) this;
		}

		public Criteria andSelectedLessThan(Boolean value) {
			addCriterion("selected <", value, "selected");
			return (Criteria) this;
		}

		public Criteria andSelectedLessThanOrEqualTo(Boolean value) {
			addCriterion("selected <=", value, "selected");
			return (Criteria) this;
		}

		public Criteria andSelectedIn(List<Boolean> values) {
			addCriterion("selected in", values, "selected");
			return (Criteria) this;
		}

		public Criteria andSelectedNotIn(List<Boolean> values) {
			addCriterion("selected not in", values, "selected");
			return (Criteria) this;
		}

		public Criteria andSelectedBetween(Boolean value1, Boolean value2) {
			addCriterion("selected between", value1, value2, "selected");
			return (Criteria) this;
		}

		public Criteria andSelectedNotBetween(Boolean value1, Boolean value2) {
			addCriterion("selected not between", value1, value2, "selected");
			return (Criteria) this;
		}
	}

	public static class Criterion {
		private String condition;
		private Object value;
		private Object secondValue;
		private boolean noValue;
		private boolean singleValue;
		private boolean betweenValue;
		private boolean listValue;
		private String typeHandler;

		public String getCondition() {
			return condition;
		}

		public Object getValue() {
			return value;
		}

		public Object getSecondValue() {
			return secondValue;
		}

		public boolean isNoValue() {
			return noValue;
		}

		public boolean isSingleValue() {
			return singleValue;
		}

		public boolean isBetweenValue() {
			return betweenValue;
		}

		public boolean isListValue() {
			return listValue;
		}

		public String getTypeHandler() {
			return typeHandler;
		}

		protected Criterion(String condition) {
			super();
			this.condition = condition;
			this.typeHandler = null;
			this.noValue = true;
		}

		protected Criterion(String condition, Object value, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.typeHandler = typeHandler;
			if (value instanceof List<?>) {
				this.listValue = true;
			} else {
				this.singleValue = true;
			}
		}

		protected Criterion(String condition, Object value) {
			this(condition, value, null);
		}

		protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.secondValue = secondValue;
			this.typeHandler = typeHandler;
			this.betweenValue = true;
		}

		protected Criterion(String condition, Object value, Object secondValue) {
			this(condition, value, secondValue, null);
		}
	}

	/**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table display
     *
     * @mbg.generated do_not_delete_during_merge Tue Sep 18 17:12:42 CST 2018
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }
}