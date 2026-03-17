import { ThumbsUp, ThumbsDown, PanelRightOpen } from "lucide-react";

interface RightPanelProps {
  collapsed?: boolean;
  onToggle?: () => void;
}

export function RightPanel({ collapsed = false, onToggle }: RightPanelProps) {
  if (collapsed) {
    return (
      <aside
        className="hidden lg:flex h-full w-12 shrink-0 flex-col items-center justify-center py-4 bg-[#1a1d24] border-l border-white/[0.06] cursor-pointer hover:bg-[#202432] transition-colors"
        onClick={onToggle}
        role="button"
        tabIndex={0}
        onKeyDown={(e) => e.key === "Enter" && onToggle?.()}
        aria-label="Ouvrir le panneau droit En ligne"
      >
        <PanelRightOpen className="w-5 h-5 text-[#b5bac1] rotate-180" aria-hidden />
      </aside>
    );
  }

  return (
    <aside
      className="hidden lg:flex h-full w-[280px] min-w-[280px] xl:w-[320px] xl:min-w-[320px] shrink-0 flex-col bg-[#1a1d24] border-l border-white/[0.06]"
      aria-label="Panneau droit"
    >
      <div className="shrink-0 px-4 py-3 border-b border-white/[0.06]">
        <h2 className="text-base font-semibold text-[#f2f3f5]">En ligne</h2>
      </div>

      <div className="flex-1 min-h-0 overflow-y-auto p-4 space-y-4">
        {/* Carte "Cette section pourrait être améliorée" */}
        <div className="p-4 rounded-xl bg-[#202432] border border-white/[0.06]">
          <p className="text-sm text-[#b5bac1] mb-4">
            Cette section pourrait être améliorée avec de nouvelles fonctionnalités.
          </p>
          <div className="flex gap-2">
            <button
              type="button"
              className="flex-1 flex items-center justify-center gap-2 h-9 rounded-lg bg-[#5865F2] text-white text-sm font-medium hover:bg-[#4752c4] transition-colors"
            >
              <ThumbsUp className="w-4 h-4" />
              Oui, j&apos;en suis !
            </button>
            <button
              type="button"
              className="flex-1 flex items-center justify-center gap-2 h-9 rounded-lg bg-[#202432] text-[#b5bac1] text-sm font-medium hover:bg-white/[0.06] border border-white/[0.06] transition-colors"
            >
              <ThumbsDown className="w-4 h-4" />
              Non merci
            </button>
          </div>
        </div>

        {/* Bloc "Tout est calme..." */}
        <div className="p-4 rounded-xl bg-[#202432] border border-white/[0.06]">
          <p className="text-sm text-[#949ba4] leading-relaxed">
            Tout est calme... pour le moment. Quand un ami sera en ligne, il apparaîtra ici.
          </p>
        </div>
      </div>
    </aside>
  );
}
