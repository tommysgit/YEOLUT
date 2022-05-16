package CvLut.MediaProject.Service;

import CvLut.MediaProject.Domain.*;
import CvLut.MediaProject.Dto.BoardDto;
import CvLut.MediaProject.Dto.FeatureDto;
import CvLut.MediaProject.Repository.*;
import CvLut.MediaProject.Repository.Board.BoardQueryRepository;
import CvLut.MediaProject.Repository.Board.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;
    private final FeatureQueryRepository featureQueryRepository;
    private final LutImageRepository lutImageRepository;
    private final OriginImageRepository originImageRepository;
    private final BoardLutImageRepository boardLutImageRepository;
    private final BoardOriginImageRepository boardOriginImageRepository;
   // private final S3Upload s3Upload;

    @Transactional
    public BoardDto.BoardDetailResDto boardDetail(Long boardIdx){
        BoardDto.BoardDetailDto boardDetailDto = boardQueryRepository.BoardInfo(boardIdx).get(0);
        List<FeatureDto.DefaultFeature> boardFeatureList = featureQueryRepository.getBoardFeatureList(boardIdx);

        Optional<Board> board = boardRepository.findById(boardIdx);
        boardRepository.save(board.get());
        return BoardDto.BoardDetailResDto.builder()
                .boardIdx(boardDetailDto.getBordIdx())
                .title(boardDetailDto.getTitle())
                .downloadCount(boardDetailDto.getDownloadCount())
                .createdAt(boardDetailDto.getCreatedAt())
                .description(boardDetailDto.getDescription())
                .source(boardDetailDto.getSource())
                .userIdx(boardDetailDto.getUserIdx()). name(boardDetailDto.getName())
                .lutUrl(boardDetailDto.getLutUrl()). profileImageUrl(boardDetailDto.getProfileImageUrl())
                .likeCount(boardDetailDto.getLikeCount()).featureList(boardFeatureList).build();
    }
    @Transactional
    public void insertBoard(BoardDto.UploadBoardReqDto uploadBoardReqDto ){
        LutImage lutImage = LutImage.builder().lutUrl(uploadBoardReqDto.getLutImageUrl()).build();
        OriginImage originImage = OriginImage.builder().originImageUrl(uploadBoardReqDto.getOriginImageUrl()).build();
        Board board = Board.builder().title(uploadBoardReqDto.getTitle()).lutFileUrl(uploadBoardReqDto.getLutFileUrl())
                .source(uploadBoardReqDto.getSource()).description(uploadBoardReqDto.getDescription()).build();
        LutImage savedLutImage = lutImageRepository.save(lutImage);
        OriginImage savedOriginImage = originImageRepository.save(originImage);
        Board savedBoard = boardRepository.save(board);
        BoardOriginImage boardOriginImage = BoardOriginImage.builder().originImage(savedOriginImage)
                .board(savedBoard).build();
        BoardLutImage boardLutImage = BoardLutImage.builder().lutImage(savedLutImage)
                .board(savedBoard).build();
        boardLutImageRepository.save(boardLutImage);
        boardOriginImageRepository.save(boardOriginImage);

    }
//    public BoardDto.s3UploadFileResDto s3Upload(MultipartFile multipartFile, String sort)  throws IOException {
//        String url = s3Upload.upload(multipartFile, sort);
//        BoardDto.s3UploadFileResDto s3UploadFileResDto = new BoardDto.s3UploadFileResDto(url);
//    }
}
