import { Search } from "lucide-react";

interface SearchInputProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  className?: string;
  "aria-label"?: string;
}

export function SearchInput({
  value,
  onChange,
  placeholder = "Recherche ou lance une conversation",
  className = "",
  "aria-label": ariaLabel = "Rechercher",
}: SearchInputProps) {
  return (
    <div className={`relative ${className}`}>
      <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#4e5058] pointer-events-none" aria-hidden />
      <input
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        aria-label={ariaLabel}
        className="w-full h-9 pl-9 pr-3 rounded-md bg-[#1e1f22] border-0 text-[#f2f3f5] placeholder:text-[#4e5058] text-sm focus:outline-none focus:ring-2 focus:ring-[#5865F2] focus:ring-inset"
      />
    </div>
  );
}
