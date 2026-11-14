// ffp: FFP-AVF-PAYLOAD edge-agent cpp
#include <sys/socket.h>
#include <linux/vm_sockets.h>
#include <unistd.h>
#include <cstring>
#include <string>

namespace {
constexpr unsigned int kVsockPort = 4242;
constexpr int kMaxFramesPerConn = 64;

void handle_orchestration_stream(int client_fd) {
    char buffer[4096];
    const char* ack = "[TIANXIA_GUEST] Sovereign Cognitive Cell v2.0.0 Operational.\n";
    (void)send(client_fd, ack, std::strlen(ack), 0);

    int frames = 0;
    while (frames < kMaxFramesPerConn) {
        ssize_t bytes = recv(client_fd, buffer, sizeof(buffer) - 1, 0);
        if (bytes <= 0) {
            break;
        }
        frames++;
        buffer[bytes] = '\0';
        std::string cmd(buffer);
        if (cmd.find("SHUTDOWN") == 0) {
            break;
        }
        if (cmd.find("EXECUTE_SWARM") == 0) {
            const char* reply = "[TIANXIA_GUEST] Workload slice accepted.\n";
            (void)send(client_fd, reply, std::strlen(reply), 0);
        }
    }
    close(client_fd);
}
}  // namespace

extern "C" int AVF_PayloadMain(int max_connections) {
    int server_fd = socket(AF_VSOCK, SOCK_STREAM, 0);
    if (server_fd < 0) {
        return -1;
    }
    struct sockaddr_vm sa_server {};
    sa_server.svm_family = AF_VSOCK;
    sa_server.svm_cid = VMADDR_CID_ANY;
    sa_server.svm_port = kVsockPort;
    if (bind(server_fd, reinterpret_cast<struct sockaddr*>(&sa_server), sizeof(sa_server)) < 0) {
        close(server_fd);
        return -1;
    }
    if (listen(server_fd, 8) < 0) {
        close(server_fd);
        return -1;
    }
    int accepted = 0;
    int limit = max_connections > 0 ? max_connections : 1024;
    while (accepted < limit) {
        struct sockaddr_vm sa_client {};
        socklen_t client_len = sizeof(sa_client);
        int client_fd = accept(server_fd, reinterpret_cast<struct sockaddr*>(&sa_client), &client_len);
        if (client_fd < 0) {
            break;
        }
        accepted++;
        handle_orchestration_stream(client_fd);
    }
    close(server_fd);
    return 0;
}
