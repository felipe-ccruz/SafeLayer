import { useMemo, useState } from "react";
import { Plus, Boxes } from "lucide-react";
import { VMCard, type VM, type VMOS } from "./components/vm-card";
import { CreateVMModal } from "./components/create-vm-modal";
import { VMViewer } from "./components/vm-viewer";

export default function App() {
  const [vms, setVms] = useState<VM[]>([
    { id: "1", name: "Trabalho", os: "windows11", status: "stopped", createdAt: Date.now() - 86400000 },
    { id: "2", name: "Testes", os: "windows10", status: "running", createdAt: Date.now() - 3600000 },
  ]);
  const [creating, setCreating] = useState(false);
  const [openId, setOpenId] = useState<string | null>(null);

  const openVM = useMemo(() => vms.find((v) => v.id === openId) ?? null, [vms, openId]);

  const createVM = (name: string, os: VMOS) => {
    setVms((prev) => [
      { id: crypto.randomUUID(), name, os, status: "stopped", createdAt: Date.now() },
      ...prev,
    ]);
    setCreating(false);
  };

  const toggleVM = (id: string) => {
    setVms((prev) =>
      prev.map((v) => (v.id === id ? { ...v, status: v.status === "running" ? "stopped" : "running" } : v))
    );
  };

  const deleteVM = (id: string) => {
    setVms((prev) => prev.filter((v) => v.id !== id));
    if (openId === id) setOpenId(null);
  };

  return (
    <div className="size-full min-h-screen bg-slate-50">
      <header className="border-b border-slate-200 bg-white/80 backdrop-blur-sm sticky top-0 z-10">
        <div className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-xl bg-slate-900 flex items-center justify-center">
              <Boxes className="w-5 h-5 text-white" />
            </div>
            <div>
              <div className="text-slate-900">Minhas VMs</div>
              <div className="text-xs text-slate-500">{vms.length} {vms.length === 1 ? "máquina" : "máquinas"}</div>
            </div>
          </div>
          <button
            onClick={() => setCreating(true)}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-slate-900 text-white hover:bg-slate-800 transition-colors"
          >
            <Plus className="w-4 h-4" />
            <span className="text-sm">Nova VM</span>
          </button>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-6 py-8">
        {vms.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-24 text-center">
            <div className="w-16 h-16 rounded-2xl bg-slate-100 flex items-center justify-center mb-4">
              <Boxes className="w-8 h-8 text-slate-400" />
            </div>
            <h2 className="text-slate-900 mb-1">Nenhuma VM por aqui</h2>
            <p className="text-slate-500 mb-6">Crie sua primeira máquina virtual em poucos cliques.</p>
            <button
              onClick={() => setCreating(true)}
              className="flex items-center gap-2 px-5 py-3 rounded-xl bg-slate-900 text-white hover:bg-slate-800 transition-colors"
            >
              <Plus className="w-4 h-4" />
              <span className="text-sm">Criar VM</span>
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {vms.map((vm) => (
              <VMCard key={vm.id} vm={vm} onToggle={toggleVM} onDelete={deleteVM} onOpen={setOpenId} />
            ))}
          </div>
        )}
      </main>

      <CreateVMModal open={creating} onClose={() => setCreating(false)} onCreate={createVM} />
      <VMViewer vm={openVM} onClose={() => setOpenId(null)} onToggle={toggleVM} />
    </div>
  );
}
