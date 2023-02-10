angular.module('announcements.services').factory('AnnouncementService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.userMgmt + '/announcement/:announcementId/:action', {}, {
        get: {
            method: 'GET',
            params: {
                action: 'get',
                announcementId: '@announcementId'
            }
        },
        getUnread: {
            method: 'GET',
            params: {
                action: 'unread'
            }
        },
        getUnpublished: {
            method: 'GET',
            isArray: true,
            params: {
                action: 'unpublished'
            }
        },
        getAllAnnouncements: {
            method: 'GET',
            isArray: true,
            params: {
                action: 'all'
            }
        },
        deleteById: {
            method: 'POST',
            params: {
                action: 'delete',
                announcementId: '@announcementId'
            }
        },
        publish: {
            method: 'PUT',
            params: {
                action: 'publish',
                announcementId: '@announcementId'
            }
        },
        save: {
            method: 'POST',
            params: {
                action: 'save'
            }
        },
        copy: {
            method: 'POST',
            params: {
                action: 'copy',
                announcementId: '@announcementId'
            }
        },
        cancel: {
            method: 'POST',
            params: {
                action: 'cancel',
                announcementId: '@announcementId'
            }
        },
        markAsRead: {
            method: 'POST',
            params: {
                action: 'markAsRead',
                announcementId: '@announcementId'
            }
		}
    });
}]);