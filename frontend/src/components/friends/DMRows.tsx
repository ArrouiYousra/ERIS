import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";

export function DMRows() {
  const navigate = useNavigate();

  return (
    <div className="dm-bar relative h-full w-14 flex items-center flex-col">
      <button
        type="button"
        className="flex items-center justify-center w-12 h-12 max-sm:w-10 max-sm:h-10 rounded-full bg-[#1a1d24] text-[#3ba55d] hover:rounded-xl hover:bg-[#3ba55d] hover:text-white transition-all duration-200 shrink-0 mt-1"
        title="Retour"
        aria-label="Retour"
        onClick={() => navigate("/app")}
      >
        <ArrowLeft className="w-5 h-5" />
      </button>
    </div>
  );
}
