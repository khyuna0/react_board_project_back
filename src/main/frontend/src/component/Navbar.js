import "./Navbar.css";
import { Link } from "react-router-dom";

function Navbar({ onLogout, user }) {
  return (
    <nav className="navbar">
      <div className="logo">Company 홈페이지</div>
      <div className="menu">
        <Link to="/">Home</Link>
        <Link to="/board">게시판</Link>
        {!user && <Link to="/login">로그인</Link>}
        {!user && <Link to="/signup">회원가입</Link>}
        {user && (
          <button className="logout_btn" onClick={onLogout}>
            로그아웃
          </button>
        )}
      </div>
    </nav>
  );
}

export default Navbar;
