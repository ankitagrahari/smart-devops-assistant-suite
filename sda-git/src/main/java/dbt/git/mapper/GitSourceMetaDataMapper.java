package dbt.git.mapper;

import dbt.git.dto.GitSourceMetaDataDetailsDTO;
import dbt.git.entites.GitSourceMetaData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GitSourceMetaDataMapper {

    public static GitSourceMetaDataDetailsDTO toDTO(GitSourceMetaData obj){
        return new GitSourceMetaDataDetailsDTO(obj.getPath(), obj.getType(), obj.getSha(), obj.getSize(), obj.getUrl());
    }

    public static List<GitSourceMetaDataDetailsDTO> toDTOList(List<GitSourceMetaData> list){
        List<GitSourceMetaDataDetailsDTO> dtoList = new ArrayList<>();
        for(GitSourceMetaData obj: list){
            dtoList.add(toDTO(obj));
        }
        return dtoList;
    }

    public static GitSourceMetaData toEntity(GitSourceMetaDataDetailsDTO dto){
        return new GitSourceMetaData(dto.path(), dto.type(), dto.sha(), dto.size(), dto.url());
    }

    public static List<GitSourceMetaData> toEntities(List<GitSourceMetaDataDetailsDTO> dtoList){
        List<GitSourceMetaData> objList = new ArrayList<>();
        for(GitSourceMetaDataDetailsDTO dto: dtoList){
            objList.add(toEntity(dto));
        }
        return objList;
    }
}
