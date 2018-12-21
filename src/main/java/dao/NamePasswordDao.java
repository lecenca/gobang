package dao;

import org.apache.ibatis.annotations.Param;

public interface NamePasswordDao {

    String getPassword(String name);

    void insertAccount(@Param("ac")String ac, @Param("pw")String pw);
}
