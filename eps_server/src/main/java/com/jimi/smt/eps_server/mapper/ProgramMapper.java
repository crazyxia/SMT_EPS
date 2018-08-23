package com.jimi.smt.eps_server.mapper;

import com.jimi.smt.eps_server.entity.Program;
import com.jimi.smt.eps_server.entity.ProgramExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProgramMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table program
     *
     * @mbg.generated Mon Aug 06 18:42:35 CST 2018
     */
    long countByExample(ProgramExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table program
     *
     * @mbg.generated Mon Aug 06 18:42:35 CST 2018
     */
    int deleteByExample(ProgramExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table program
     *
     * @mbg.generated Mon Aug 06 18:42:35 CST 2018
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table program
     *
     * @mbg.generated Mon Aug 06 18:42:35 CST 2018
     */
    int insert(Program record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table program
     *
     * @mbg.generated Mon Aug 06 18:42:35 CST 2018
     */
    int insertSelective(Program record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table program
     *
     * @mbg.generated Mon Aug 06 18:42:35 CST 2018
     */
    List<Program> selectByExample(ProgramExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table program
     *
     * @mbg.generated Mon Aug 06 18:42:35 CST 2018
     */
    Program selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table program
     *
     * @mbg.generated Mon Aug 06 18:42:35 CST 2018
     */
    int updateByExampleSelective(@Param("record") Program record, @Param("example") ProgramExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table program
     *
     * @mbg.generated Mon Aug 06 18:42:35 CST 2018
     */
    int updateByExample(@Param("record") Program record, @Param("example") ProgramExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table program
     *
     * @mbg.generated Mon Aug 06 18:42:35 CST 2018
     */
    int updateByPrimaryKeySelective(Program record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table program
     *
     * @mbg.generated Mon Aug 06 18:42:35 CST 2018
     */
    int updateByPrimaryKey(Program record);
}