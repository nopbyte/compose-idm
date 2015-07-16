package de.passau.uni.sec.compose.id.core.persistence.repository;

import java.util.Date;
import java.util.List;

import de.passau.uni.sec.compose.id.core.persistence.entities.Code;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends JpaRepository<Code, String> {
	
	public List<Code> findByCodeAndType(String code,String type);
	
	
    @Query("FROM Code c where c.lastModified < ?1 ")
	public List<Code> findByLastModifiedBefore(Date since);
}