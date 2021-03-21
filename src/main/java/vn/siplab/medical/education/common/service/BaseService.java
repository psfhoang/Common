package vn.siplab.medical.education.common.service;


import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.siplab.medical.education.common.dto.BaseDTO;

public interface BaseService<DTO extends BaseDTO> {

  DTO save(Long id, DTO dto);

  DTO save(Long id, Map<String, Object> dto);

  DTO save(DTO dto);

  List<DTO> save(List<DTO> dtos);

  void delete(Long id);

  List<DTO> delete(List<Long> ids);

  DTO findById(Long id);

  DTO findById(Long id, boolean mapAllProperties);

  List<DTO> findAll();

  boolean existsById(Long id);

  default Path importData(MultipartFile file, int sheetNo, int startLineNo) {
    return importData(file, sheetNo, startLineNo, null);
  }

  Path importData(MultipartFile file, int sheetNo, int startLineNo, String... relativePath);

  Page<DTO> search(DTO dto, Pageable pageable);

  DTO search2(DTO dto);

  DTO search2(DTO dto, Pageable pageable);

//  ReportDTO export(DTO dto, Pageable pageable);
}