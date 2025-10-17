import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axiosConfig";
import "./BoardWrite.css";

function BoardWrite({ user }) {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [errors, setErrors] = useState({});
  const navigate = useNavigate();

  // 글쓰기
  const handleSubmit = async (e) => {
    e.preventDefault(); // 페이지 새로고침 방지
    setErrors({});
    // 로그인한 유저만 글쓰기 허용
    if (!user) {
      // T -> 로그인하지 않은 경우
      alert("로그인 후 글 작성 가능합니다.");
      return;
    }
    try {
      await api.post("/api/board", { title, content });
      alert("글 작성 완료");
      navigate("/board");
    } catch (err) {
      if (err.response && err.response.status === 400) {
        setErrors(err.response.data);
      }
      console.error("글 쓰기 실패 :" + err);
    }
  };

  const handleWrite = () => {
    // 로그인한 유저만 글쓰기 허용
    if (!user) {
      // T -> 로그인하지 않은 경우
      alert("로그인 후 글 작성 가능합니다.");
      navigate("/board");
    }
  };

  useEffect(() => {
    handleWrite();
  }, []);

  return (
    <div className="write-container">
      <h2>글쓰기</h2>
      <form onSubmit={handleSubmit} className="write-form">
        <input
          type="text"
          placeholder="제목"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        <textarea
          placeholder="내용"
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />
        <div className="button-group">
          <button type="submit">작성</button>
          <button type="button" onClick={() => navigate("/board")}>
            취소
          </button>
        </div>
      </form>
      {errors.title && <p style={{ color: "red" }}>{errors.title}</p>}
      {errors.content && <p style={{ color: "red" }}>{errors.content}</p>}
    </div>
  );
}

export default BoardWrite;
