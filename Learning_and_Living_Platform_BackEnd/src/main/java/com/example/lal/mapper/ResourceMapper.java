package com.example.lal.mapper;

import com.example.lal.model.domain.DownloadStatistic;
import com.example.lal.model.domain.Resource;
import com.example.lal.model.entity.ResourceCategoryEntry;
import com.example.lal.model.entity.ResourceDetail;
import com.example.lal.model.entity.ResourceSummary;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ResourceMapper {
        @Select({
                        "<script>",
                        "SELECT COUNT(*)",
                        "FROM (",
                        "   SELECT DISTINCT Resource.id",
                        "   FROM ResourceCategories AS a, Resource",
                        "   WHERE a.resourceId = Resource.id",
                        "   AND Resource.subject IN",
                        "   <foreach item='item' collection='subjects' open='(' separator=',' close=')'>",
                        "       #{item}",
                        "   </foreach>",
                        "   AND a.category IN",
                        "   <foreach item='item' collection='categories' open='(' separator=',' close=')'>",
                        "       #{item}",
                        "   </foreach>",
                        ") AS subquery",
                        "</script>"
        })
        int getResourceBySubjectsAndCategoriesCount(int[] subjects,
                        int[] categories);

        @Insert("INSERT INTO Resource(userId, title, publishTime, subject,content, path, imageUrl, size, fileName) " +
                        "VALUES (#{userId}, #{title},  #{publishTime}, #{subject},#{content}, #{path}, #{imageUrl}, #{size}, #{fileName});")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int createResource(Resource resource);

        @Insert("INSERT INTO ResourceCategories(resourceId, category) VALUES(#{resourceId}, #{category});")
        int createResourceCategories(int resourceId, int category);

        @Delete("DELETE FROM Resource WHERE id=#{resourceId};")
        int deleteResource(String resourceId);

        @Select("SELECT * FROM Resource WHERE id=#{resourceId};")
        ResourceDetail readResource(@Param("resourceId") String resourceId);

        @Select({
                        "<script>",
                        "SELECT DISTINCT Resource.id,Resource.userId,Resource.title,Resource.content,Resource.subject ",
                        "FROM",
                        "(SELECT *",
                        "FROM ResourceCategories",
                        "WHERE category IN",
                        "<foreach item='item' collection='categories' open='(' separator=',' close=')'>",
                        "#{item}",
                        "</foreach> ) AS a, Resource",
                        "WHERE a.resourceId = Resource.id",
                        "AND Resource.subject IN",
                        "<foreach item='item' collection='subjects' open='(' separator=',' close=')'>",
                        "#{item}",
                        "</foreach>",
                        "LIMIT #{filter}, #{cntInPage}",
                        "</script>"
        })
        List<ResourceSummary> getResourcesBySubjectAndCategoriesByPage(@Param("subjects") int[] subjects,
                        @Param("categories") int[] categories,
                        @Param("filter") int filter,
                        @Param("cntInPage") int cntInPage);

        @Update("UPDATE Resource SET downloadCount=#{downloadCount}," +
                        "userId = #{userId}," +
                        "title = #{title}," +
                        "subject = #{subject}," +
                        "content = #{content}," +
                        "imageUrl = #{imageUrl}," +
                        "path = #{path}," +
                        "size = #{size}" +
                        " WHERE id=#{id};")
        void updateResource(ResourceDetail resourceDetail);

        @Delete("DELETE FROM ResourceCategories where resourceId = #{resourceId}")
        void deleteResourceCategories(int resourceId);

        @Select("SELECT * FROM ResourceCategories " +
                        "WHERE resourceId = #{id}")
        List<ResourceCategoryEntry> getResourceCategoriesById(int id);

        @Select({ "<script>",
                        "SELECT count(*) ",
                        "from",
                        "(SELECT *",
                        "FROM ResourceCategories",
                        "WHERE category = #{category} ) as a,Resource",
                        "Where a.resourceId=Resource.id",
                        "and Resource.subject = #{subject}",
                        "</script>" })
        int getResourceNumByCategoryAndSubject(int category, int subject);

        @Select("SELECT COUNT(*) AS count " +
                        "FROM Resource " +
                        "WHERE publishTime >= #{startTime} AND publishTime <= #{endTime}")
        int getResourceCountByTime(LocalDateTime startTime, LocalDateTime endTime);

        @Select("SELECT COUNT(*) " +
                        "FROM Resource " +
                        "WHERE subject = #{subject}")
        int getResourceCountBySubject(int subject);

        @Select("SELECT DISTINCT category " +
                        "FROM ResourceCategories " +
                        "WHERE resourceId = #{resourceId}")
        int[] readResourceCategories(String resourceId);

        @Select({
                        "<script>",
                        "SELECT DISTINCT b.id,b.userId,b.title,b.content,b.subject,b.downloadCount",
                        "FROM",
                        "   (SELECT *",
                        "   FROM ResourceCategories",
                        "   WHERE category IN",
                        "       <foreach item='item' collection='categories' open='(' separator=',' close=')'>",
                        "       #{item}",
                        "       </foreach> ) AS a, " +
                                        "       (SELECT * FROM Resource WHERE MATCH(title, content) AGAINST(#{keywords} IN BOOLEAN MODE))AS b ",
                        "WHERE a.resourceId = b.id ",
                        "   AND b.subject IN ",
                        "   <foreach item='item' collection='subjects' open='(' separator=',' close=')'> ",
                        "   #{item} ",
                        "   </foreach>",
                        "ORDER BY b.downloadCount DESC " +
                                        "LIMIT #{filter}, #{cntInPage} " +
                                        "</script>"
        })
        List<ResourceSummary> searchResourcesBySubjectAndCategoriesByPage(@Param("keywords") String keywords,
                        @Param("subjects") int[] subjects,
                        @Param("categories") int[] categories,
                        @Param("filter") int filter,
                        @Param("cntInPage") int cntInPage);

        @Select({
                        "<script>" +
                                        "select count(*) from" +
                                        "(" +
                                        "SELECT DISTINCT b.id",
                        "FROM",
                        "   (SELECT *",
                        "   FROM ResourceCategories",
                        "   WHERE category IN",
                        "       <foreach item='item' collection='categories' open='(' separator=',' close=')'>",
                        "       #{item}",
                        "       </foreach> ) AS a, " +
                                        "       (SELECT * FROM Resource WHERE MATCH(title, content) AGAINST(#{keywords} IN BOOLEAN MODE))AS b ",
                        "WHERE a.resourceId = b.id ",
                        "   AND b.subject IN ",
                        "   <foreach item='item' collection='subjects' open='(' separator=',' close=')'> ",
                        "   #{item} ",
                        "   </foreach>) as c",
                        "</script>"
        })
        int searchResourceBySubjectsAndCategoriesCount(String keywords, int[] subjects, int[] categories);

        @Select("SELECT * from Resource where id = #{resourceId}")
        ResourceSummary getResourceSummary(int resourceId);

        @Select("<script> " +
                        "SELECT * FROM Resource " +
                        "<if test='resourceIDs != null and resourceIDs.size() > 0'> " +
                        "WHERE id NOT IN " +
                        "<foreach item='item' collection='resourceIDs' open='(' separator=',' close=')'> " +
                        "#{item} " +
                        "</foreach> " +
                        "</if> " +
                        "ORDER BY RAND() " +
                        "LIMIT #{size} " +
                        "</script>")

        List<ResourceSummary> getResourceSummaryListRandom(@Param("size") int size,
                        @Param("resourceIDs") List<Integer> resourceIDs);

        @Select("SELECT count(*) FROM Resource ")
        int getAllResourceCount();

        @Select("SELECT count(*) from Resource " +
                        "where userId = #{userId} ")
        int getResourceCountByUserId(int userId);

        @Select("SELECT * from Resource " +
                        "where userId = #{userId} " +
                        "ORDER BY publishTime DESC " +
                        "LIMIT #{filterCount}, #{pageSize}")
        List<ResourceSummary> getResourceSummaryByUserId(int userId, int filterCount, int pageSize);

        // TODO:推荐页的函数。要重新设计传入变量。

}
